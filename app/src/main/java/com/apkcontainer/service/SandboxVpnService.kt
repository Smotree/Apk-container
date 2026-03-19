package com.apkcontainer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.room.Room
import com.apkcontainer.MainActivity
import com.apkcontainer.data.db.AppDatabase
import com.apkcontainer.data.db.entity.NetworkLogEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.InetAddress
import java.nio.ByteBuffer

class SandboxVpnService : VpnService() {

    companion object {
        private const val TAG = "SandboxVpnService"
        private const val CHANNEL_ID = "vpn_monitoring"
        private const val NOTIFICATION_ID = 1
        const val ACTION_START = "com.apkcontainer.VPN_START"
        const val ACTION_STOP = "com.apkcontainer.VPN_STOP"
    }

    private var vpnInterface: ParcelFileDescriptor? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var monitorJob: Job? = null
    private lateinit var database: AppDatabase
    private var packetCount = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        // Create Room DB manually — this service runs in :vpn process, no Hilt
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "apk_container.db"
        ).build()
        Log.d(TAG, "Database initialized in :vpn process")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand action=${intent?.action}")
        when (intent?.action) {
            ACTION_STOP -> {
                Log.d(TAG, "Stopping VPN")
                stopVpn()
                return START_NOT_STICKY
            }
            else -> {
                Log.d(TAG, "Starting VPN")
                startForeground(NOTIFICATION_ID, createNotification())
                startVpn()
            }
        }
        return START_STICKY
    }

    private fun startVpn() {
        try {
            val builder = Builder()
                .setSession("APK Container Monitor")
                .addAddress("10.0.0.2", 32)
                .addRoute("0.0.0.0", 0)
                .addDnsServer("8.8.8.8")
                .addDnsServer("8.8.4.4")
                .setMtu(1500)

            try {
                builder.addDisallowedApplication(packageName)
            } catch (e: Exception) {
                Log.w(TAG, "Could not exclude own package: ${e.message}")
            }

            Log.d(TAG, "Calling establish()...")
            vpnInterface = builder.establish()

            if (vpnInterface != null) {
                Log.d(TAG, "VPN interface established successfully")
                startMonitoring()
            } else {
                Log.e(TAG, "establish() returned null")
                stopSelf()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting VPN", e)
        }
    }

    private fun startMonitoring() {
        monitorJob = scope.launch {
            val vpnFd = vpnInterface ?: return@launch
            val input = FileInputStream(vpnFd.fileDescriptor)
            val output = FileOutputStream(vpnFd.fileDescriptor)
            val buffer = ByteBuffer.allocate(32767)

            Log.d(TAG, "Monitoring started — reading packets")

            try {
                while (true) {
                    buffer.clear()
                    val length = input.read(buffer.array())
                    if (length <= 0) continue

                    val packetData = buffer.array().copyOf(length)

                    val parsed = PacketParser.parsePacket(packetData)
                    if (parsed != null) {
                        logNetworkEvent(parsed)
                    }

                    // Forward the packet (passthrough)
                    output.write(packetData, 0, length)
                }
            } catch (e: Exception) {
                Log.d(TAG, "VPN monitoring stopped: ${e.message}")
            }
        }
    }

    private fun logNetworkEvent(packet: ParsedPacket) {
        scope.launch {
            try {
                val host = try {
                    InetAddress.getByName(packet.destinationAddress).canonicalHostName
                } catch (e: Exception) {
                    ""
                }

                val isSuspicious = PacketParser.isSuspicious(
                    packet.destinationAddress,
                    packet.destinationPort
                )

                val protocol = if (PacketParser.isDnsPacket(packet)) "DNS" else packet.protocol

                val event = NetworkLogEntity(
                    appId = 0,
                    packageName = "unknown",
                    remoteAddress = packet.destinationAddress,
                    remoteHost = host,
                    remotePort = packet.destinationPort,
                    protocol = protocol,
                    bytesSent = packet.payloadSize.toLong(),
                    bytesReceived = 0,
                    timestamp = System.currentTimeMillis(),
                    isSuspicious = isSuspicious
                )

                database.networkLogDao().insertEvent(event)
                packetCount++

                if (packetCount % 50 == 0L) {
                    Log.d(TAG, "Logged $packetCount packets so far")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error logging network event", e)
            }
        }
    }

    private fun stopVpn() {
        monitorJob?.cancel()
        vpnInterface?.close()
        vpnInterface = null
        Log.d(TAG, "VPN stopped. Total packets logged: $packetCount")
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        stopVpn()
        scope.cancel()
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "VPN-мониторинг",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Отслеживание сетевой активности приложений"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("APK Контейнер")
            .setContentText("Мониторинг сетевой активности активен")
            .setSmallIcon(android.R.drawable.ic_menu_view)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .build()
    }
}
