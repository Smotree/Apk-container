package com.apkcontainer.data.apk

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import com.apkcontainer.domain.model.PermissionInfo
import com.apkcontainer.domain.model.SandboxApp
import com.apkcontainer.util.PermissionDescriptions
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class ApkAnalysisResult(
    val app: SandboxApp,
    val permissions: List<PermissionInfo>,
    val warnings: List<String>,
    val riskScore: Int,
    val icon: Drawable?
)

@Singleton
class ApkAnalyzer @Inject constructor(
    private val context: Context
) {
    fun analyzeApk(apkPath: String): ApkAnalysisResult? {
        val file = File(apkPath)
        if (!file.exists()) return null

        val pm = context.packageManager
        val flags = PackageManager.GET_PERMISSIONS or
                PackageManager.GET_ACTIVITIES or
                PackageManager.GET_SERVICES or
                PackageManager.GET_RECEIVERS or
                PackageManager.GET_META_DATA

        val packageInfo: PackageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageArchiveInfo(
                apkPath,
                PackageManager.PackageInfoFlags.of(flags.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageArchiveInfo(apkPath, flags)
        } ?: return null

        // Required for icon loading
        packageInfo.applicationInfo?.let {
            it.sourceDir = apkPath
            it.publicSourceDir = apkPath
        }

        val appName = packageInfo.applicationInfo?.let {
            pm.getApplicationLabel(it).toString()
        } ?: packageInfo.packageName

        val icon = try {
            packageInfo.applicationInfo?.loadIcon(pm)
        } catch (e: Exception) {
            null
        }

        val versionName = packageInfo.versionName ?: "unknown"
        val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }

        // Parse permissions
        val requestedPermissions = packageInfo.requestedPermissions ?: emptyArray()
        val permissions = requestedPermissions.map { perm ->
            PermissionDescriptions.getPermissionInfo(perm)
        }.sortedByDescending { it.riskLevel.score }

        // Find suspicious combinations
        val warnings = PermissionDescriptions.findSuspiciousCombinations(
            requestedPermissions.toList()
        )

        // Calculate risk score
        val riskScore = PermissionDescriptions.calculateRiskScore(permissions, warnings)

        val activitiesCount = packageInfo.activities?.size ?: 0
        val servicesCount = packageInfo.services?.size ?: 0
        val receiversCount = packageInfo.receivers?.size ?: 0

        val app = SandboxApp(
            packageName = packageInfo.packageName,
            appName = appName,
            versionName = versionName,
            versionCode = versionCode,
            apkPath = apkPath,
            riskScore = riskScore,
            permissions = permissions,
            activitiesCount = activitiesCount,
            servicesCount = servicesCount,
            receiversCount = receiversCount
        )

        return ApkAnalysisResult(
            app = app,
            permissions = permissions,
            warnings = warnings,
            riskScore = riskScore,
            icon = icon
        )
    }
}
