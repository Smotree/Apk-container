package com.apkcontainer.sandbox

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

enum class SandboxStatus {
    WORK_PROFILE_ACTIVE,   // Full isolation — apps run in separate profile
    NOT_CONFIGURED         // No isolation — apps run normally
}

@Singleton
class SandboxManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val TAG = "SandboxManager"
    }

    private val devicePolicyManager: DevicePolicyManager
        get() = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private val adminComponent: ComponentName
        get() = SandboxDeviceAdmin.getComponentName(context)

    /**
     * Check if our app is the profile owner (Work Profile is active)
     */
    fun isProfileOwner(): Boolean {
        return devicePolicyManager.isProfileOwnerApp(context.packageName)
    }

    /**
     * Get current sandbox status
     */
    fun getSandboxStatus(): SandboxStatus {
        return if (isProfileOwner()) {
            SandboxStatus.WORK_PROFILE_ACTIVE
        } else {
            SandboxStatus.NOT_CONFIGURED
        }
    }

    /**
     * Create intent to provision a managed Work Profile.
     * This will ask the user to create a separate work space.
     */
    fun createProvisioningIntent(): Intent {
        return Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE).apply {
            putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                adminComponent
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                putExtra(
                    DevicePolicyManager.EXTRA_PROVISIONING_ALLOW_OFFLINE,
                    true
                )
            }
        }
    }

    /**
     * Check if the device supports Work Profile provisioning
     */
    fun canProvisionWorkProfile(): Boolean {
        val intent = createProvisioningIntent()
        return intent.resolveActivity(context.packageManager) != null
    }

    /**
     * Get ADB command for manual profile owner setup (alternative method)
     */
    fun getAdbSetupCommand(): String {
        return "adb shell dpm set-profile-owner ${context.packageName}/${adminComponent.className}"
    }
}
