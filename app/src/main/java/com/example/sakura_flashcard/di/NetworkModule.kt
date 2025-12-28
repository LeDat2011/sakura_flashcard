package com.example.sakura_flashcard.di

import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.api.RefreshTokenRequest
import com.example.sakura_flashcard.data.auth.AuthTokenManager
import com.example.sakura_flashcard.data.network.SecurityInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Lazy
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Authenticator
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private val okhttp3.Response.responseCount: Int
        get() {
            var result = 1
            var lastResponse = priorResponse
            while (lastResponse != null) {
                result++
                lastResponse = lastResponse.priorResponse
            }
            return result
        }

    // Dynamic BASE_URL - auto-detect emulator vs real device
    private val BASE_URL: String
        get() {
            val isEmulator = android.os.Build.FINGERPRINT.startsWith("generic") ||
                    android.os.Build.FINGERPRINT.startsWith("unknown") ||
                    android.os.Build.MODEL.contains("google_sdk") ||
                    android.os.Build.MODEL.contains("Emulator") ||
                    android.os.Build.MODEL.contains("Android SDK") ||
                    android.os.Build.HARDWARE.contains("goldfish") ||
                    android.os.Build.HARDWARE.contains("ranchu")
            
            return if (isEmulator) {
                "http://10.0.2.2:3000/api/" // Android Emulator -> localhost
            } else {
                "http://192.168.54.101:3000/api/" // Real device -> your PC IP (change this to your PC's IP)
            }
        }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            android.util.Log.d("🌸 API", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    @Named("AuthInterceptor")
    fun provideAuthInterceptor(tokenManager: AuthTokenManager): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            
            // Skip auth for login/register/refresh endpoints
            val path = originalRequest.url.encodedPath
            if (path.contains("auth/login") || 
                path.contains("auth/register") || 
                path.contains("auth/refresh-token")) {
                return@Interceptor chain.proceed(originalRequest)
            }

            val accessToken = tokenManager.getAccessToken()
            
            val newRequest = if (accessToken != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $accessToken")
                    .build()
            } else {
                originalRequest
            }
            
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    fun provideAuthenticator(
        tokenManager: AuthTokenManager,
        @Named("AuthApiService") authApiService: Lazy<ApiService>
    ): Authenticator {
        return Authenticator { _, response ->
            // Only try to refresh 3 times
            if (response.request.header("Authorization") != null && response.responseCount >= 3) {
                return@Authenticator null
            }

            synchronized(this) {
                val refreshToken = tokenManager.getRefreshToken()
                val currentToken = tokenManager.getAccessToken()

                if (refreshToken == null) return@Authenticator null

                // If token was already refreshed by another thread, retry with new token
                val authHeader = response.request.header("Authorization")
                if (authHeader != "Bearer $currentToken") {
                    return@Authenticator response.request.newBuilder()
                        .header("Authorization", "Bearer $currentToken")
                        .build()
                }

                // Call refresh token API
                try {
                    val refreshResponse = authApiService.get().refreshTokenBlocking(RefreshTokenRequest(refreshToken)).execute()
                    if (refreshResponse.isSuccessful && refreshResponse.body()?.success == true) {
                        val tokenData = refreshResponse.body()?.data
                        if (tokenData != null) {
                            tokenManager.saveTokens(
                                accessToken = tokenData.accessToken,
                                refreshToken = tokenData.refreshToken,
                                expiresIn = 900, // Should ideally come from server
                                userId = tokenManager.getUserId() ?: ""
                            )
                            return@Authenticator response.request.newBuilder()
                                .header("Authorization", "Bearer ${tokenData.accessToken}")
                                .build()
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("🌸 NetworkModule", "Token refresh failed", e)
                }

                // If refresh fails, clear tokens and logout
                tokenManager.clearTokens()
                null
            }
        }
    }

    @Provides
    @Singleton
    fun provideCertificatePinner(): CertificatePinner {
        return CertificatePinner.Builder()
            // TODO: Thay bằng SHA-256 hash thật của server certificate
            // .add("10.0.2.2", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        @Named("AuthInterceptor") authInterceptor: Interceptor,
        securityInterceptor: SecurityInterceptor,
        authenticator: Authenticator,
        certificatePinner: CertificatePinner
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(securityInterceptor) // Security checks first
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            .certificatePinner(certificatePinner)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthOkHttpClient")
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(
        @Named("AuthOkHttpClient") okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    @Named("AuthApiService")
    fun provideAuthApiService(@Named("AuthRetrofit") retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}