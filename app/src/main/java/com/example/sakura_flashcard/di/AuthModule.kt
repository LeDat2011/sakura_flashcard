package com.example.sakura_flashcard.di

import android.content.Context
import com.example.sakura_flashcard.data.api.ApiService
import com.example.sakura_flashcard.data.auth.AuthRepository
import com.example.sakura_flashcard.data.auth.AuthTokenManager
import com.example.sakura_flashcard.data.auth.BiometricAuthManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthTokenManager(
        @ApplicationContext context: Context
    ): AuthTokenManager {
        return AuthTokenManager(context)
    }

    @Provides
    @Singleton
    fun provideBiometricAuthManager(
        @ApplicationContext context: Context
    ): BiometricAuthManager {
        return BiometricAuthManager(context)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        authTokenManager: AuthTokenManager,
        biometricAuthManager: BiometricAuthManager
    ): AuthRepository {
        return AuthRepository(apiService, authTokenManager, biometricAuthManager)
    }
}