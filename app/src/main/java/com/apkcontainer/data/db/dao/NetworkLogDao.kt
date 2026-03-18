package com.apkcontainer.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.apkcontainer.data.db.entity.NetworkLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NetworkLogDao {
    @Query("SELECT * FROM network_logs WHERE appId = :appId ORDER BY timestamp DESC")
    fun getEventsForApp(appId: Long): Flow<List<NetworkLogEntity>>

    @Query("SELECT * FROM network_logs ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<NetworkLogEntity>>

    @Insert
    suspend fun insertEvent(event: NetworkLogEntity)

    @Query("DELETE FROM network_logs WHERE appId = :appId")
    suspend fun deleteEventsForApp(appId: Long)

    @Query("SELECT COUNT(*) FROM network_logs WHERE appId = :appId")
    suspend fun getEventCountForApp(appId: Long): Int
}
