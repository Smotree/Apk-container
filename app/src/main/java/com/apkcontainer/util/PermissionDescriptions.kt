package com.apkcontainer.util

import com.apkcontainer.domain.model.PermissionInfo
import com.apkcontainer.domain.model.RiskLevel

object PermissionDescriptions {

    private data class PermDef(
        val label: String,
        val description: String,
        val riskLevel: RiskLevel,
        val group: String
    )

    private val knownPermissions = mapOf(
        // CRITICAL
        "android.permission.SEND_SMS" to PermDef(
            "Отправка SMS", "Может отправлять SMS (возможно платные!)",
            RiskLevel.CRITICAL, "SMS"
        ),
        "android.permission.CALL_PHONE" to PermDef(
            "Телефонные звонки", "Может совершать звонки без подтверждения",
            RiskLevel.CRITICAL, "Телефон"
        ),
        "android.permission.INSTALL_PACKAGES" to PermDef(
            "Установка приложений", "Может устанавливать другие приложения",
            RiskLevel.CRITICAL, "Система"
        ),
        "android.permission.BIND_DEVICE_ADMIN" to PermDef(
            "Администратор устройства", "Может управлять настройками безопасности",
            RiskLevel.CRITICAL, "Система"
        ),
        "android.permission.BIND_ACCESSIBILITY_SERVICE" to PermDef(
            "Специальные возможности", "Может контролировать экран и действия",
            RiskLevel.CRITICAL, "Система"
        ),
        "android.permission.SYSTEM_ALERT_WINDOW" to PermDef(
            "Наложение поверх окон", "Может показывать окна поверх других приложений",
            RiskLevel.CRITICAL, "Система"
        ),
        "android.permission.WRITE_SETTINGS" to PermDef(
            "Изменение настроек", "Может изменять системные настройки",
            RiskLevel.CRITICAL, "Система"
        ),

        // HIGH
        "android.permission.READ_SMS" to PermDef(
            "Чтение SMS", "Может читать ваши SMS-сообщения",
            RiskLevel.HIGH, "SMS"
        ),
        "android.permission.RECEIVE_SMS" to PermDef(
            "Получение SMS", "Может перехватывать входящие SMS",
            RiskLevel.HIGH, "SMS"
        ),
        "android.permission.READ_CONTACTS" to PermDef(
            "Чтение контактов", "Может читать список ваших контактов",
            RiskLevel.HIGH, "Контакты"
        ),
        "android.permission.WRITE_CONTACTS" to PermDef(
            "Изменение контактов", "Может изменять ваши контакты",
            RiskLevel.HIGH, "Контакты"
        ),
        "android.permission.READ_CALL_LOG" to PermDef(
            "Журнал звонков", "Может читать историю звонков",
            RiskLevel.HIGH, "Телефон"
        ),
        "android.permission.CAMERA" to PermDef(
            "Камера", "Может делать фото и видео",
            RiskLevel.HIGH, "Камера"
        ),
        "android.permission.RECORD_AUDIO" to PermDef(
            "Микрофон", "Может записывать звук",
            RiskLevel.HIGH, "Микрофон"
        ),
        "android.permission.ACCESS_FINE_LOCATION" to PermDef(
            "Точное местоположение", "Может определять ваше точное местоположение",
            RiskLevel.HIGH, "Местоположение"
        ),
        "android.permission.READ_EXTERNAL_STORAGE" to PermDef(
            "Чтение файлов", "Может читать файлы на устройстве",
            RiskLevel.HIGH, "Файлы"
        ),
        "android.permission.WRITE_EXTERNAL_STORAGE" to PermDef(
            "Запись файлов", "Может создавать и изменять файлы",
            RiskLevel.HIGH, "Файлы"
        ),
        "android.permission.READ_PHONE_STATE" to PermDef(
            "Состояние телефона", "Может читать номер телефона и IMEI",
            RiskLevel.HIGH, "Телефон"
        ),
        "android.permission.READ_PHONE_NUMBERS" to PermDef(
            "Номер телефона", "Может читать ваш номер телефона",
            RiskLevel.HIGH, "Телефон"
        ),

        // MEDIUM
        "android.permission.ACCESS_COARSE_LOCATION" to PermDef(
            "Примерное местоположение", "Может определять примерное местоположение",
            RiskLevel.MEDIUM, "Местоположение"
        ),
        "android.permission.ACCESS_BACKGROUND_LOCATION" to PermDef(
            "Местоположение в фоне", "Может отслеживать местоположение в фоновом режиме",
            RiskLevel.HIGH, "Местоположение"
        ),
        "android.permission.READ_CALENDAR" to PermDef(
            "Чтение календаря", "Может читать события календаря",
            RiskLevel.MEDIUM, "Календарь"
        ),
        "android.permission.WRITE_CALENDAR" to PermDef(
            "Изменение календаря", "Может изменять события календаря",
            RiskLevel.MEDIUM, "Календарь"
        ),
        "android.permission.RECEIVE_BOOT_COMPLETED" to PermDef(
            "Автозапуск", "Запускается автоматически при включении устройства",
            RiskLevel.MEDIUM, "Система"
        ),
        "android.permission.FOREGROUND_SERVICE" to PermDef(
            "Фоновая работа", "Может работать в фоновом режиме",
            RiskLevel.MEDIUM, "Система"
        ),
        "android.permission.REQUEST_INSTALL_PACKAGES" to PermDef(
            "Запрос установки", "Может запрашивать установку приложений",
            RiskLevel.MEDIUM, "Система"
        ),
        "android.permission.BLUETOOTH" to PermDef(
            "Bluetooth", "Может использовать Bluetooth",
            RiskLevel.MEDIUM, "Подключения"
        ),
        "android.permission.BLUETOOTH_CONNECT" to PermDef(
            "Bluetooth подключение", "Может подключаться к Bluetooth устройствам",
            RiskLevel.MEDIUM, "Подключения"
        ),
        "android.permission.NFC" to PermDef(
            "NFC", "Может использовать NFC",
            RiskLevel.MEDIUM, "Подключения"
        ),
        "android.permission.USE_BIOMETRIC" to PermDef(
            "Биометрия", "Может использовать отпечаток пальца или Face ID",
            RiskLevel.MEDIUM, "Безопасность"
        ),

        // LOW
        "android.permission.INTERNET" to PermDef(
            "Интернет", "Может отправлять и получать данные из сети",
            RiskLevel.LOW, "Сеть"
        ),
        "android.permission.ACCESS_NETWORK_STATE" to PermDef(
            "Состояние сети", "Может проверять подключение к интернету",
            RiskLevel.LOW, "Сеть"
        ),
        "android.permission.ACCESS_WIFI_STATE" to PermDef(
            "Состояние Wi-Fi", "Может проверять подключение к Wi-Fi",
            RiskLevel.LOW, "Сеть"
        ),
        "android.permission.VIBRATE" to PermDef(
            "Вибрация", "Может использовать вибрацию",
            RiskLevel.LOW, "Устройство"
        ),
        "android.permission.WAKE_LOCK" to PermDef(
            "Предотвращение сна", "Может предотвращать выключение экрана",
            RiskLevel.LOW, "Устройство"
        ),
        "android.permission.POST_NOTIFICATIONS" to PermDef(
            "Уведомления", "Может отправлять уведомления",
            RiskLevel.LOW, "Уведомления"
        ),
    )

    // Suspicious combinations
    data class SuspiciousCombination(
        val permissions: Set<String>,
        val warning: String
    )

    val suspiciousCombinations = listOf(
        SuspiciousCombination(
            setOf("android.permission.READ_SMS", "android.permission.INTERNET"),
            "Приложение может читать SMS и отправлять их содержимое в интернет"
        ),
        SuspiciousCombination(
            setOf("android.permission.SEND_SMS", "android.permission.INTERNET"),
            "Приложение может отправлять платные SMS и данные в интернет"
        ),
        SuspiciousCombination(
            setOf("android.permission.READ_CONTACTS", "android.permission.INTERNET"),
            "Приложение может отправлять ваши контакты в интернет"
        ),
        SuspiciousCombination(
            setOf("android.permission.CAMERA", "android.permission.RECORD_AUDIO"),
            "Приложение может записывать видео и звук одновременно"
        ),
        SuspiciousCombination(
            setOf("android.permission.ACCESS_FINE_LOCATION", "android.permission.INTERNET"),
            "Приложение может отправлять ваше местоположение в интернет"
        ),
        SuspiciousCombination(
            setOf("android.permission.READ_PHONE_STATE", "android.permission.INTERNET"),
            "Приложение может отправлять данные о вашем телефоне в интернет"
        ),
        SuspiciousCombination(
            setOf("android.permission.CAMERA", "android.permission.INTERNET"),
            "Приложение может отправлять фото/видео в интернет"
        ),
        SuspiciousCombination(
            setOf("android.permission.RECORD_AUDIO", "android.permission.INTERNET"),
            "Приложение может отправлять аудиозаписи в интернет"
        ),
    )

    fun getPermissionInfo(permission: String): PermissionInfo {
        val def = knownPermissions[permission]
        return if (def != null) {
            PermissionInfo(
                permission = permission,
                label = def.label,
                description = def.description,
                riskLevel = def.riskLevel,
                group = def.group
            )
        } else {
            val shortName = permission.substringAfterLast(".")
            PermissionInfo(
                permission = permission,
                label = shortName,
                description = permission,
                riskLevel = RiskLevel.LOW,
                group = "Другое"
            )
        }
    }

    fun findSuspiciousCombinations(permissions: List<String>): List<String> {
        val permSet = permissions.toSet()
        return suspiciousCombinations
            .filter { it.permissions.all { p -> p in permSet } }
            .map { it.warning }
    }

    fun calculateRiskScore(permissions: List<PermissionInfo>, warnings: List<String>): Int {
        if (permissions.isEmpty()) return 0

        var score = 0

        // Base score from permissions
        permissions.forEach { perm ->
            score += when (perm.riskLevel) {
                RiskLevel.CRITICAL -> 20
                RiskLevel.HIGH -> 12
                RiskLevel.MEDIUM -> 5
                RiskLevel.LOW -> 1
            }
        }

        // Bonus for suspicious combinations
        score += warnings.size * 10

        return score.coerceIn(0, 100)
    }
}
