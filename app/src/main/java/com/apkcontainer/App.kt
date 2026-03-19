package com.apkcontainer

import android.app.Application
import android.content.Context
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import me.weishu.reflection.Reflection
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.app.configuration.ClientConfiguration
import java.io.File

@HiltAndroidApp
class App : Application() {

    companion object {
        private const val TAG = "ApkContainerApp"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        if (base == null) return

        try {
            // MUST unseal hidden APIs before BlackBox init
            Reflection.unseal(base)

            BlackBoxCore.get().closeCodeInit()
            BlackBoxCore.get().onBeforeMainApplicationAttach(this, base)

            // Initialize with proper ClientConfiguration
            BlackBoxCore.get().doAttachBaseContext(base, object : ClientConfiguration() {
                override fun getHostPackageName(): String {
                    return base.packageName
                }

                override fun isEnableDaemonService(): Boolean = true
                override fun isEnableLauncherActivity(): Boolean = false
                override fun isHideRoot(): Boolean = false
                override fun isUseVpnNetwork(): Boolean = false
                override fun isDisableFlagSecure(): Boolean = false

                override fun requestInstallPackage(file: File?, userId: Int): Boolean = false
            })

            BlackBoxCore.get().onAfterMainApplicationAttach(this, base)
            Log.d(TAG, "BlackBox attached successfully")
        } catch (e: Exception) {
            Log.e(TAG, "BlackBox attach error: ${e.message}", e)
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            BlackBoxCore.get().doCreate()
            Log.d(TAG, "BlackBox engine initialized")
        } catch (e: Exception) {
            Log.e(TAG, "BlackBox init error: ${e.message}", e)
        }
    }
}
