package com.apkcontainer.domain.repository

import com.apkcontainer.domain.model.NetworkEvent
import kotlinx.coroutines.flow.Flow

interface NetworkRepository {
    fun getEventsForApp(appId: Long): Flow<List<NetworkEvent>>
    fun getAllEvents(): Flow<List<NetworkEvent>>
    suspend fun insertEvent(event: NetworkEvent)
    suspend fun deleteEventsForApp(appId: Long)
    suspend fun getEventCountForApp(appId: Long): Int
}
