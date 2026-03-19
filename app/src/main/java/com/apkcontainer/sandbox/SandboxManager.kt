package com.apkcontainer.sandbox

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import top.niunaijun.blackbox.BlackBoxCore
import javax.inject.Inject
import javax.inject.Singleton

enum class SandboxStatus {
    ACTIVE,       // BlackBox engine is running — full isolation
    NOT_READY     // BlackBox failed to initialize
}

@Singleton
class SandboxManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getSandboxStatus(): SandboxStatus {
        return try {
            // Check if BlackBox is initialized
            BlackBoxCore.get()
            SandboxStatus.ACTIVE
        } catch (e: Exception) {
            SandboxStatus.NOT_READY
        }
    }

    fun isActive(): Boolean = getSandboxStatus() == SandboxStatus.ACTIVE
}
