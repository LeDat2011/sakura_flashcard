package com.example.sakura_flashcard.data.network

import com.example.sakura_flashcard.util.DatabaseSecurityManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp Interceptor that performs security checks before each network request.
 * 
 * This interceptor adds custom headers indicating the device's security status
 * and can optionally reject requests from insecure environments.
 */
@Singleton
class SecurityInterceptor @Inject constructor(
    private val securityManager: DatabaseSecurityManager
) : Interceptor {

    companion object {
        private const val HEADER_DEVICE_ROOTED = "X-Device-Rooted"
        private const val HEADER_IS_EMULATOR = "X-Is-Emulator"
        private const val HEADER_IS_DEBUGGED = "X-Is-Debugged"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Perform security checks
        val securityStatus = securityManager.performSecurityChecks()
        
        // Add security headers to all requests
        // Server can use these to implement additional security policies
        val newRequest = originalRequest.newBuilder()
            .header(HEADER_DEVICE_ROOTED, securityStatus.isRooted.toString())
            .header(HEADER_IS_EMULATOR, securityStatus.isEmulator.toString())
            .header(HEADER_IS_DEBUGGED, securityStatus.isDebugged.toString())
            .build()
        
        // Optionally, you could reject requests from rooted/debugged devices
        // For now, we just add headers and let the server decide
        // if (!securityStatus.isSecure) {
        //     throw SecurityException("Request blocked: Device security compromised")
        // }
        
        return chain.proceed(newRequest)
    }
}
