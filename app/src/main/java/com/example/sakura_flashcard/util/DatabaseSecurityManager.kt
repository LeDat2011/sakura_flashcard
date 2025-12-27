package com.example.sakura_flashcard.util

import android.content.Context
import android.os.Build
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Security utilities for database encryption and device integrity checks.
 * 
 * Responsibilities:
 * - Generate and securely store database passphrase using Android Keystore.
 * - Perform root/emulator detection for session security.
 */
@Singleton
class DatabaseSecurityManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREF_NAME = "sakura_security_prefs"
        private const val PASSPHRASE_KEY = "db_passphrase"
        private const val PASSPHRASE_LENGTH = 32
    }

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    /**
     * Get or generate the database passphrase.
     * The passphrase is generated once and stored securely in EncryptedSharedPreferences.
     */
    fun getDatabasePassphrase(): ByteArray {
        val storedPassphrase = encryptedPrefs.getString(PASSPHRASE_KEY, null)
        
        return if (storedPassphrase != null) {
            storedPassphrase.toByteArray(Charsets.UTF_8)
        } else {
            val newPassphrase = generateSecurePassphrase()
            encryptedPrefs.edit()
                .putString(PASSPHRASE_KEY, String(newPassphrase, Charsets.UTF_8))
                .apply()
            newPassphrase
        }
    }

    /**
     * Generate a cryptographically secure random passphrase.
     */
    private fun generateSecurePassphrase(): ByteArray {
        val random = SecureRandom()
        val passphrase = ByteArray(PASSPHRASE_LENGTH)
        random.nextBytes(passphrase)
        // Convert to Base64-like string for storage compatibility
        return android.util.Base64.encode(passphrase, android.util.Base64.NO_WRAP)
    }

    // ==================== Device Integrity Checks ====================

    /**
     * Check if the device is potentially rooted.
     * Returns true if any root indicators are detected.
     */
    fun isDeviceRooted(): Boolean {
        return checkRootBinaries() || checkSuProcess() || checkRootManagementApps()
    }

    private fun checkRootBinaries(): Boolean {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/data/local/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/system/app/Superuser.apk"
        )
        return paths.any { File(it).exists() }
    }

    private fun checkSuProcess(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("which", "su"))
            val result = process.inputStream.bufferedReader().readText()
            process.destroy()
            result.isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }

    private fun checkRootManagementApps(): Boolean {
        val rootApps = listOf(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.topjohnwu.magisk"
        )
        val pm = context.packageManager
        return rootApps.any { packageName ->
            try {
                pm.getPackageInfo(packageName, 0)
                true
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Check if the app is running on an emulator (for debug/testing purposes).
     */
    fun isRunningOnEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_gphone")
                || Build.PRODUCT.contains("vbox86p")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
    }

    /**
     * Check if the app is being debugged.
     */
    fun isBeingDebugged(): Boolean {
        return android.os.Debug.isDebuggerConnected() || android.os.Debug.waitingForDebugger()
    }

    /**
     * Perform all security checks.
     * Returns a SecurityStatus object with all check results.
     */
    fun performSecurityChecks(): SecurityStatus {
        return SecurityStatus(
            isRooted = isDeviceRooted(),
            isEmulator = isRunningOnEmulator(),
            isDebugged = isBeingDebugged()
        )
    }
}

/**
 * Data class representing the result of security checks.
 */
data class SecurityStatus(
    val isRooted: Boolean,
    val isEmulator: Boolean,
    val isDebugged: Boolean
) {
    val isSecure: Boolean
        get() = !isRooted && !isDebugged
    
    val warningMessage: String?
        get() = when {
            isRooted -> "Thiết bị đã root, dữ liệu có thể không an toàn."
            isDebugged -> "Ứng dụng đang bị debug."
            else -> null
        }
}
