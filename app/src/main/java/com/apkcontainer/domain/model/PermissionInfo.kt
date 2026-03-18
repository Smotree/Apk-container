package com.apkcontainer.domain.model

data class PermissionInfo(
    val permission: String,
    val label: String,
    val description: String,
    val riskLevel: RiskLevel,
    val group: String = ""
)
