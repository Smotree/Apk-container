package com.apkcontainer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.apkcontainer.domain.model.NetworkEvent
import com.apkcontainer.ui.theme.RiskCritical
import com.apkcontainer.ui.theme.SuspiciousOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NetworkEventItem(
    event: NetworkEvent,
    modifier: Modifier = Modifier
) {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = if (event.isSuspicious) {
            CardDefaults.cardColors(containerColor = RiskCritical.copy(alpha = 0.08f))
        } else {
            CardDefaults.cardColors()
        }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (event.isSuspicious) Icons.Default.Warning else Icons.Default.CloudUpload,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = if (event.isSuspicious) SuspiciousOrange else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.remoteHost.ifEmpty { event.remoteAddress },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (event.isSuspicious) SuspiciousOrange else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${event.protocol} :${event.remotePort} • ${formatBytes(event.bytesSent + event.bytesReceived)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = timeFormat.format(Date(event.timestamp)),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatBytes(bytes: Long): String {
    return when {
        bytes < 1024 -> "$bytes B"
        bytes < 1024 * 1024 -> "${bytes / 1024} KB"
        else -> "${bytes / (1024 * 1024)} MB"
    }
}
