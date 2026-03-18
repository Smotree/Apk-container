package com.apkcontainer.sandbox

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SandboxInstaller @Inject constructor(
    private val context: Context
) {
    /**
     * Install APK using system installer.
     * If Work Profile is active, the app will be installed in the work profile.
     * Otherwise, it will be installed normally with user confirmation.
     */
    fun installApk(apkPath: String): Intent {
        val file = File(apkPath)
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )

        return Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
            putExtra(Intent.EXTRA_RETURN_RESULT, true)
        }
    }

    /**
     * Uninstall an app by package name
     */
    fun uninstallApk(packageName: String): Intent {
        val uri = Uri.parse("package:$packageName")
        return Intent(Intent.ACTION_DELETE, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
    }

    /**
     * Check if a package is installed on the device
     */
    fun isPackageInstalled(packageName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    packageName,
                    android.content.pm.PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(packageName, 0)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
