package com.apkcontainer.sandbox

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.UserHandle
import android.util.Log

class SandboxDeviceAdmin : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "SandboxDeviceAdmin"

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, SandboxDeviceAdmin::class.java)
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.d(TAG, "Device admin enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.d(TAG, "Device admin disabled")
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)
        Log.d(TAG, "Work profile provisioning complete")

        // Enable the work profile
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = getComponentName(context)

        // Set the profile name
        dpm.setProfileName(componentName, "APK Контейнер")

        // Enable the profile
        dpm.setProfileEnabled(componentName)

        Log.d(TAG, "Work profile enabled and ready")
    }
}
