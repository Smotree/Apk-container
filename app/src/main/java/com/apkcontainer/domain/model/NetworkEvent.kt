package com.apkcontainer.domain.model

data class NetworkEvent(
    val id: Long = 0,
    val appId: Long,
    val packageName: String,
    val remoteAddress: String,
    val remoteHost: String = "",
    val remotePort: Int,
    val protocol: String, // TCP, UDP, DNS
    val bytesSent: Long = 0,
    val bytesReceived: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuspicious: Boolean = false
)
