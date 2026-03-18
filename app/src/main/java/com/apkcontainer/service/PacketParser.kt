package com.apkcontainer.service

import java.net.InetAddress
import java.nio.ByteBuffer

data class ParsedPacket(
    val sourceAddress: String,
    val destinationAddress: String,
    val sourcePort: Int,
    val destinationPort: Int,
    val protocol: String, // TCP, UDP
    val payloadSize: Int
)

object PacketParser {

    private const val IP_HEADER_MIN_SIZE = 20
    private const val TCP_HEADER_MIN_SIZE = 20
    private const val UDP_HEADER_SIZE = 8

    /**
     * Parse an IP packet from raw bytes
     */
    fun parsePacket(data: ByteArray): ParsedPacket? {
        if (data.size < IP_HEADER_MIN_SIZE) return null

        val buffer = ByteBuffer.wrap(data)

        // IP header
        val versionAndIHL = buffer.get().toInt() and 0xFF
        val version = versionAndIHL shr 4
        if (version != 4) return null // Only IPv4

        val ihl = (versionAndIHL and 0x0F) * 4
        if (data.size < ihl) return null

        buffer.position(9) // Protocol field
        val protocol = buffer.get().toInt() and 0xFF

        // Total length
        buffer.position(2)
        val totalLength = buffer.short.toInt() and 0xFFFF

        // Source IP
        buffer.position(12)
        val srcIpBytes = ByteArray(4)
        buffer.get(srcIpBytes)
        val srcIp = InetAddress.getByAddress(srcIpBytes).hostAddress ?: "0.0.0.0"

        // Destination IP
        val dstIpBytes = ByteArray(4)
        buffer.get(dstIpBytes)
        val dstIp = InetAddress.getByAddress(dstIpBytes).hostAddress ?: "0.0.0.0"

        // Transport layer
        if (data.size < ihl + 4) return null
        buffer.position(ihl)

        val srcPort = buffer.short.toInt() and 0xFFFF
        val dstPort = buffer.short.toInt() and 0xFFFF

        val protocolName = when (protocol) {
            6 -> "TCP"
            17 -> "UDP"
            else -> "OTHER"
        }

        val headerSize = when (protocol) {
            6 -> ihl + TCP_HEADER_MIN_SIZE
            17 -> ihl + UDP_HEADER_SIZE
            else -> ihl
        }
        val payloadSize = (totalLength - headerSize).coerceAtLeast(0)

        return ParsedPacket(
            sourceAddress = srcIp,
            destinationAddress = dstIp,
            sourcePort = srcPort,
            destinationPort = dstPort,
            protocol = protocolName,
            payloadSize = payloadSize
        )
    }

    /**
     * Determine if a DNS query is being made
     */
    fun isDnsPacket(packet: ParsedPacket): Boolean {
        return packet.destinationPort == 53 || packet.sourcePort == 53
    }

    /**
     * Detect potentially suspicious connections
     */
    fun isSuspicious(destinationAddress: String, destinationPort: Int): Boolean {
        // Non-standard ports for HTTP traffic
        val suspiciousPorts = setOf(4444, 5555, 6666, 7777, 8888, 9999, 1337, 31337)
        if (destinationPort in suspiciousPorts) return true

        // Tor exit nodes and known malicious ranges would be checked here
        // For now, flag non-standard HTTPS/HTTP ports
        if (destinationPort !in setOf(80, 443, 8080, 8443, 53) && destinationPort > 1024) {
            return false // Don't flag all high ports, only known bad ones
        }

        return false
    }
}
