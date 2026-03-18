package com.apkcontainer.data.repository

import com.apkcontainer.data.db.dao.InstalledAppDao
import com.apkcontainer.data.db.entity.InstalledAppEntity
import com.apkcontainer.domain.model.PermissionInfo
import com.apkcontainer.domain.model.RiskLevel
import com.apkcontainer.domain.model.SandboxApp
import com.apkcontainer.domain.repository.AppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepositoryImpl @Inject constructor(
    private val dao: InstalledAppDao
) : AppRepository {

    override fun getAllApps(): Flow<List<SandboxApp>> {
        return dao.getAllApps().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getAppById(id: Long): SandboxApp? {
        return dao.getAppById(id)?.toDomain()
    }

    override suspend fun getAppByPackageName(packageName: String): SandboxApp? {
        return dao.getAppByPackageName(packageName)?.toDomain()
    }

    override suspend fun insertApp(app: SandboxApp): Long {
        return dao.insertApp(app.toEntity())
    }

    override suspend fun updateApp(app: SandboxApp) {
        dao.updateApp(app.toEntity())
    }

    override suspend fun deleteApp(app: SandboxApp) {
        dao.deleteApp(app.toEntity())
    }

    private fun InstalledAppEntity.toDomain(): SandboxApp {
        return SandboxApp(
            id = id,
            packageName = packageName,
            appName = appName,
            versionName = versionName,
            versionCode = versionCode,
            apkPath = apkPath,
            installedAt = installedAt,
            riskScore = riskScore,
            permissions = parsePermissions(permissionsJson),
            activitiesCount = activitiesCount,
            servicesCount = servicesCount,
            receiversCount = receiversCount,
            isInstalledInSandbox = isInstalledInSandbox
        )
    }

    private fun SandboxApp.toEntity(): InstalledAppEntity {
        return InstalledAppEntity(
            id = id,
            packageName = packageName,
            appName = appName,
            versionName = versionName,
            versionCode = versionCode,
            apkPath = apkPath,
            installedAt = installedAt,
            riskScore = riskScore,
            permissionsJson = serializePermissions(permissions),
            activitiesCount = activitiesCount,
            servicesCount = servicesCount,
            receiversCount = receiversCount,
            isInstalledInSandbox = isInstalledInSandbox
        )
    }

    private fun serializePermissions(permissions: List<PermissionInfo>): String {
        val jsonArray = JSONArray()
        permissions.forEach { perm ->
            val obj = JSONObject().apply {
                put("permission", perm.permission)
                put("label", perm.label)
                put("description", perm.description)
                put("riskLevel", perm.riskLevel.name)
                put("group", perm.group)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    private fun parsePermissions(json: String): List<PermissionInfo> {
        if (json.isEmpty()) return emptyList()
        return try {
            val jsonArray = JSONArray(json)
            (0 until jsonArray.length()).map { i ->
                val obj = jsonArray.getJSONObject(i)
                PermissionInfo(
                    permission = obj.getString("permission"),
                    label = obj.getString("label"),
                    description = obj.getString("description"),
                    riskLevel = RiskLevel.valueOf(obj.getString("riskLevel")),
                    group = obj.optString("group", "")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
