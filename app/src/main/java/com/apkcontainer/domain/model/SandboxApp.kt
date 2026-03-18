package com.apkcontainer.domain.model

data class SandboxApp(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val apkPath: String,
    val installedAt: Long = System.currentTimeMillis(),
    val riskScore: Int = 0,
    val permissions: List<PermissionInfo> = emptyList(),
    val activitiesCount: Int = 0,
    val servicesCount: Int = 0,
    val receiversCount: Int = 0,
    val isInstalledInSandbox: Boolean = false,
    val networkRequestsCount: Int = 0
)
