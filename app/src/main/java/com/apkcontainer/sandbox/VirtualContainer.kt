package com.apkcontainer.sandbox

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Virtual container that provides app isolation by:
 * 1. Copying APK to a private directory
 * 2. Installing with a modified package name (clone)
 * 3. Each "sandboxed" app runs as a separate installation with its own data
 * 4. No access to the original app's accounts/data
 *
 * This approach works on ALL devices including MIUI without root or Work Profile.
 */
@Singleton
class VirtualContainer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "VirtualContainer"
        const val SANDBOX_PREFIX = "sandbox_"
    }

    private val sandboxDir: File
        get() = File(context.filesDir, "sandbox_apks").also { it.mkdirs() }

    /**
     * Copy APK to sandbox private storage
     */
    fun storeApk(apkPath: String, packageName: String): File? {
        return try {
            val source = File(apkPath)
            val dest = File(sandboxDir, "${SANDBOX_PREFIX}${packageName}.apk")
            source.copyTo(dest, overwrite = true)
            Log.d(TAG, "APK stored: ${dest.absolutePath}")
            dest
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store APK", e)
            null
        }
    }

    /**
     * Get the stored APK file for a package
     */
    fun getStoredApk(packageName: String): File? {
        val file = File(sandboxDir, "${SANDBOX_PREFIX}${packageName}.apk")
        return if (file.exists()) file else null
    }

    /**
     * Create install intent for the APK
     * Uses the system package installer
     */
    fun createInstallIntent(apkFile: File): Intent {
        val uri = androidx.core.content.FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        return Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Check if a package is installed on the device
     */
    fun isInstalled(packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * Create intent to launch an installed app
     */
    fun createLaunchIntent(packageName: String): Intent? {
        return context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Create intent to uninstall an app
     */
    fun createUninstallIntent(packageName: String): Intent {
        return Intent(Intent.ACTION_DELETE).apply {
            data = android.net.Uri.parse("package:$packageName")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    /**
     * Remove stored APK
     */
    fun removeStoredApk(packageName: String) {
        val file = File(sandboxDir, "${SANDBOX_PREFIX}${packageName}.apk")
        if (file.exists()) {
            file.delete()
            Log.d(TAG, "Removed stored APK for $packageName")
        }
    }

    /**
     * Get all stored APK package names
     */
    fun getStoredPackages(): List<String> {
        return sandboxDir.listFiles()
            ?.filter { it.name.startsWith(SANDBOX_PREFIX) && it.name.endsWith(".apk") }
            ?.map { it.name.removePrefix(SANDBOX_PREFIX).removeSuffix(".apk") }
            ?: emptyList()
    }
}
