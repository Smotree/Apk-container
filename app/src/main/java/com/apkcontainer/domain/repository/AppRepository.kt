package com.apkcontainer.domain.repository

import com.apkcontainer.domain.model.SandboxApp
import kotlinx.coroutines.flow.Flow

interface AppRepository {
    fun getAllApps(): Flow<List<SandboxApp>>
    suspend fun getAppById(id: Long): SandboxApp?
    suspend fun getAppByPackageName(packageName: String): SandboxApp?
    suspend fun insertApp(app: SandboxApp): Long
    suspend fun updateApp(app: SandboxApp)
    suspend fun deleteApp(app: SandboxApp)
}
