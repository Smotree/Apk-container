package com.apkcontainer.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.apkcontainer.domain.model.PermissionInfo
import com.apkcontainer.domain.model.RiskLevel

@Composable
fun PermissionCard(
    permission: PermissionInfo,
    modifier: Modifier = Modifier
) {
    val riskColor = when (permission.riskLevel) {
        RiskLevel.CRITICAL -> com.apkcontainer.ui.theme.RiskCritical
        RiskLevel.HIGH -> com.apkcontainer.ui.theme.RiskHigh
        RiskLevel.MEDIUM -> com.apkcontainer.ui.theme.RiskMedium
        RiskLevel.LOW -> com.apkcontainer.ui.theme.RiskLow
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = riskColor.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getPermissionIcon(permission.group),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = riskColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permission.label,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = permission.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RiskBadge(
                riskScore = when (permission.riskLevel) {
                    RiskLevel.CRITICAL -> 80
                    RiskLevel.HIGH -> 55
                    RiskLevel.MEDIUM -> 30
                    RiskLevel.LOW -> 5
                }
            )
        }
    }
}

private fun getPermissionIcon(group: String): ImageVector {
    return when (group) {
        "SMS" -> Icons.Default.Sms
        "Телефон" -> Icons.Default.Call
        "Камера" -> Icons.Default.CameraAlt
        "Микрофон" -> Icons.Default.Mic
        "Местоположение" -> Icons.Default.LocationOn
        "Контакты" -> Icons.Default.Contacts
        "Файлы" -> Icons.Default.Folder
        "Календарь" -> Icons.Default.Today
        "Сеть" -> Icons.Default.Language
        "Подключения" -> Icons.Default.Bluetooth
        "Уведомления" -> Icons.Default.Notifications
        "Устройство" -> Icons.Default.PhoneAndroid
        "Система" -> Icons.Default.Security
        "Безопасность" -> Icons.Default.Security
        else -> Icons.Default.Warning
    }
}
