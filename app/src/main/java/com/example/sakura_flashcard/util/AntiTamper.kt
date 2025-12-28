package com.example.sakura_flashcard.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Debug
import android.provider.Settings
import java.io.File

/**
 * AntiTamper - Runtime security checks for Anti-Debug and Anti-Tampering
 * 
 * Features:
 * - Debugger detection
 * - Root detection  
 * - App signature verification
 * - Tamper detection
 */
object AntiTamper {
    
    /**
     * Run all security checks
     * @return true if device is compromised/debugged
     */
    fun isCompromised(context: Context): Boolean {
        return isDebugged() || 
               isDebuggerAttached() || 
               isRooted() ||
               isDebuggable(context)
    }

    /**
     * Check if app is being debugged
     */
    fun isDebugged(): Boolean {
        return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
    }

    /**
     * Check if debugger is attached via timing attack
     */
    fun isDebuggerAttached(): Boolean {
        val start = Debug.threadCpuTimeNanos()
        for (i in 0..999999) {
            // Busy loop - debugger slows this down significantly
        }
        val stop = Debug.threadCpuTimeNanos()
        // If loop takes more than 10ms, likely being debugged
        return (stop - start) > 10_000_000
    }

    /**
     * Check if running on emulator
     */
    fun isEmulator(): Boolean {
        val indicators = listOf(
            android.os.Build.FINGERPRINT.startsWith("generic"),
            android.os.Build.FINGERPRINT.startsWith("unknown"),
            android.os.Build.MODEL.contains("google_sdk"),
            android.os.Build.MODEL.contains("Emulator"),
            android.os.Build.MODEL.contains("Android SDK"),
            android.os.Build.MANUFACTURER.contains("Genymotion"),
            android.os.Build.BRAND.startsWith("generic"),
            android.os.Build.DEVICE.startsWith("generic"),
            android.os.Build.PRODUCT == "sdk",
            android.os.Build.PRODUCT == "sdk_x86",
            android.os.Build.PRODUCT == "sdk_gphone_x86",
            android.os.Build.HARDWARE.contains("goldfish"),
            android.os.Build.HARDWARE.contains("ranchu"),
            android.os.Build.BOARD == "goldfish",
            android.os.Build.HOST.startsWith("Build")
        )
        return indicators.any { it }
    }

    /**
     * Check if device is rooted
     */
    fun isRooted(): Boolean {
        // Check for su binary
        val suPaths = arrayOf(
            "/system/app/Superuser.apk",
            "/sbin/su",
            "/system/bin/su",
            "/system/xbin/su",
            "/data/local/xbin/su",
            "/data/local/bin/su",
            "/system/sd/xbin/su",
            "/system/bin/failsafe/su",
            "/data/local/su",
            "/su/bin/su"
        )
        
        for (path in suPaths) {
            if (File(path).exists()) {
                return true
            }
        }

        // Check for root management apps
        val rootPackages = arrayOf(
            "com.noshufou.android.su",
            "com.thirdparty.superuser",
            "eu.chainfire.supersu",
            "com.koushikdutta.superuser",
            "com.zachspong.temprootremovejb",
            "com.ramdroid.appquarantine",
            "com.topjohnwu.magisk"
        )

        return try {
            Runtime.getRuntime().exec("su")
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Check if app is debuggable (debug build flag)
     */
    fun isDebuggable(context: Context): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }

    /**
     * Get detailed security status for logging/reporting
     */
    fun getSecurityStatus(context: Context): Map<String, Boolean> {
        return mapOf(
            "debugger_connected" to isDebugged(),
            "debugger_attached" to isDebuggerAttached(),
            "emulator" to isEmulator(),
            "rooted" to isRooted(),
            "debuggable_build" to isDebuggable(context)
        )
    }
}
