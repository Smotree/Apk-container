package com.apkcontainer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.apkcontainer.R
import com.apkcontainer.ui.theme.RiskCritical
import com.apkcontainer.ui.theme.RiskHigh
import com.apkcontainer.ui.theme.RiskLow
import com.apkcontainer.ui.theme.RiskMedium

@Composable
fun RiskBadge(
    riskScore: Int,
    modifier: Modifier = Modifier
) {
    val (color, label) = when {
        riskScore < 20 -> RiskLow to stringResource(R.string.analysis_risk_low)
        riskScore < 45 -> RiskMedium to stringResource(R.string.analysis_risk_medium)
        riskScore < 70 -> RiskHigh to stringResource(R.string.analysis_risk_high)
        else -> RiskCritical to stringResource(R.string.analysis_risk_critical)
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}

fun riskColor(riskScore: Int): Color {
    return when {
        riskScore < 20 -> RiskLow
        riskScore < 45 -> RiskMedium
        riskScore < 70 -> RiskHigh
        else -> RiskCritical
    }
}
