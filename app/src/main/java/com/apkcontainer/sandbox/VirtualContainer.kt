package com.apkcontainer.sandbox

import android.content.Context
import android.content.pm.PackageInfo
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.entity.pm.InstallResult
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Virtual container using BlackBox engine.
 * Provides true process-level app isolation:
 * - Apps run in a virtual environment inside the host process
 * - No access to host's accounts, contacts, or files
 * - All data stored in host app's private directory
 * - Works on ALL devices including MIUI without root
 */
@Singleton
class VirtualContainer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "VirtualContainer"
        private const val USER_ID = 0 // Default virtual user
    }

    /**
     * Install APK into the virtual container.
     * The app will be completely isolated from the host device.
     */
    fun installApp(apkPath: String): InstallResult {
        val apkFile = File(apkPath)
        if (!apkFile.exists()) {
            return InstallResult().apply {
                // Will have success=false by default
            }
        }

        Log.d(TAG, "Installing APK into virtual container: $apkPath")
        return BlackBoxCore.get().installPackageAsUser(apkFile, USER_ID)
    }

    /**
     * Launch an app inside the virtual container.
     * The app runs in an isolated process with its own data.
     */
    fun launchApp(packageName: String): Boolean {
        Log.d(TAG, "Launching app in virtual container: $packageName")
        return BlackBoxCore.get().launchApk(packageName, USER_ID)
    }

    /**
     * Uninstall an app from the virtual container.
     * All app data within the container is removed.
     */
    fun uninstallApp(packageName: String) {
        Log.d(TAG, "Uninstalling from virtual container: $packageName")
        BlackBoxCore.get().uninstallPackageAsUser(packageName, USER_ID)
    }

    /**
     * Check if an app is installed in the virtual container.
     */
    fun isInstalled(packageName: String): Boolean {
        return BlackBoxCore.get().isInstalled(packageName, USER_ID)
    }

    /**
     * Get all apps installed in the virtual container.
     */
    fun getInstalledPackages(): List<PackageInfo> {
        return BlackBoxCore.get().getInstalledPackages(0, USER_ID)
    }

    /**
     * Clear all data for an app in the virtual container.
     */
    fun clearAppData(packageName: String) {
        BlackBoxCore.get().clearPackage(packageName, USER_ID)
    }

    /**
     * Stop an app running in the virtual container.
     */
    fun stopApp(packageName: String) {
        BlackBoxCore.get().stopPackage(packageName, USER_ID)
    }
}
