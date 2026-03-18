package com.apkcontainer.data.repository

import com.apkcontainer.data.db.dao.NetworkLogDao
import com.apkcontainer.data.db.entity.NetworkLogEntity
import com.apkcontainer.domain.model.NetworkEvent
import com.apkcontainer.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkRepositoryImpl @Inject constructor(
    private val dao: NetworkLogDao
) : NetworkRepository {

    override fun getEventsForApp(appId: Long): Flow<List<NetworkEvent>> {
        return dao.getEventsForApp(appId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAllEvents(): Flow<List<NetworkEvent>> {
        return dao.getAllEvents().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertEvent(event: NetworkEvent) {
        dao.insertEvent(event.toEntity())
    }

    override suspend fun deleteEventsForApp(appId: Long) {
        dao.deleteEventsForApp(appId)
    }

    override suspend fun getEventCountForApp(appId: Long): Int {
        return dao.getEventCountForApp(appId)
    }

    private fun NetworkLogEntity.toDomain(): NetworkEvent {
        return NetworkEvent(
            id = id,
            appId = appId,
            packageName = packageName,
            remoteAddress = remoteAddress,
            remoteHost = remoteHost,
            remotePort = remotePort,
            protocol = protocol,
            bytesSent = bytesSent,
            bytesReceived = bytesReceived,
            timestamp = timestamp,
            isSuspicious = isSuspicious
        )
    }

    private fun NetworkEvent.toEntity(): NetworkLogEntity {
        return NetworkLogEntity(
            id = id,
            appId = appId,
            packageName = packageName,
            remoteAddress = remoteAddress,
            remoteHost = remoteHost,
            remotePort = remotePort,
            protocol = protocol,
            bytesSent = bytesSent,
            bytesReceived = bytesReceived,
            timestamp = timestamp,
            isSuspicious = isSuspicious
        )
    }
}
