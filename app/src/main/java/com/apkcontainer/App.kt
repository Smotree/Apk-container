package com.apkcontainer

import android.app.Application
import android.content.Context
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import top.niunaijun.blackbox.BlackBoxCore

@HiltAndroidApp
class App : Application() {

    companion object {
        private const val TAG = "ApkContainerApp"
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        try {
            BlackBoxCore.get().closeCodeInit()
            BlackBoxCore.get().onBeforeMainApplicationAttach(this, base)
        } catch (e: Exception) {
            Log.e(TAG, "BlackBox attach error: ${e.message}")
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            BlackBoxCore.get().onAfterMainApplicationAttach(this, this)
            BlackBoxCore.get().doCreate()
            Log.d(TAG, "BlackBox engine initialized")
        } catch (e: Exception) {
            Log.e(TAG, "BlackBox init error: ${e.message}")
        }
    }
}
