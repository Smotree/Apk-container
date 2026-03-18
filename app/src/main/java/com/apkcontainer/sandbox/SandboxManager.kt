package com.apkcontainer.sandbox

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.UserManager
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SandboxManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "SandboxManager"
    }

    private val devicePolicyManager: DevicePolicyManager
        get() = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private val adminComponent: ComponentName
        get() = ComponentName(context, SandboxDeviceAdmin::class.java)

    /**
     * Check if the app is a device/profile owner (required for Work Profile management)
     */
    fun isProfileOwner(): Boolean {
        return devicePolicyManager.isProfileOwnerApp(context.packageName)
    }

    /**
     * Check if the app has device admin privileges
     */
    fun isDeviceAdmin(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponent)
    }

    /**
     * Check if Work Profile is supported and available
     */
    fun isWorkProfileSupported(): Boolean {
        val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
        return userManager.isUserRunningOrStopping(android.os.Process.myUserHandle())
    }

    /**
     * Create intent to provision a managed profile (Work Profile)
     * The user will be asked to confirm creating a work profile
     */
    fun createProvisioningIntent(): Intent {
        return Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE).apply {
            putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                adminComponent
            )
            putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_SKIP_ENCRYPTION,
                true
            )
        }
    }

    /**
     * Create intent to request device admin activation
     */
    fun createDeviceAdminIntent(): Intent {
        return Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "APK Контейнер использует администрирование устройства для создания безопасной песочницы"
            )
        }
    }

    /**
     * Get current sandbox status
     */
    fun getSandboxStatus(): SandboxStatus {
        return when {
            isProfileOwner() -> SandboxStatus.WORK_PROFILE_ACTIVE
            isDeviceAdmin() -> SandboxStatus.DEVICE_ADMIN_ONLY
            else -> SandboxStatus.NOT_CONFIGURED
        }
    }
}

enum class SandboxStatus {
    WORK_PROFILE_ACTIVE,
    DEVICE_ADMIN_ONLY,
    NOT_CONFIGURED
}
