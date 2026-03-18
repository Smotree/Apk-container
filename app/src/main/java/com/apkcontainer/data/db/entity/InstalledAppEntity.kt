package com.apkcontainer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "installed_apps")
data class InstalledAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val apkPath: String,
    val installedAt: Long,
    val riskScore: Int,
    val permissionsJson: String, // JSON serialized permissions
    val activitiesCount: Int,
    val servicesCount: Int,
    val receiversCount: Int,
    val isInstalledInSandbox: Boolean
)
