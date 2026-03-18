package com.apkcontainer.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "network_logs")
data class NetworkLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appId: Long,
    val packageName: String,
    val remoteAddress: String,
    val remoteHost: String,
    val remotePort: Int,
    val protocol: String,
    val bytesSent: Long,
    val bytesReceived: Long,
    val timestamp: Long,
    val isSuspicious: Boolean
)
