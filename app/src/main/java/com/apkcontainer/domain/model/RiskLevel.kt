package com.apkcontainer.domain.model

enum class RiskLevel(val score: Int) {
    LOW(0),
    MEDIUM(25),
    HIGH(50),
    CRITICAL(75)
}
