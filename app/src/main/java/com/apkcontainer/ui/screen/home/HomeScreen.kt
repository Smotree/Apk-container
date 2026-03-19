package com.apkcontainer.ui.screen.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.apkcontainer.R
import com.apkcontainer.sandbox.SandboxManager
import com.apkcontainer.sandbox.SandboxStatus
import com.apkcontainer.ui.components.AppCard
import com.apkcontainer.ui.theme.RiskCritical
import com.apkcontainer.ui.theme.RiskLow
import com.apkcontainer.ui.theme.RiskMedium

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onInstallClick: () -> Unit,
    onAppClick: (Long) -> Unit,
    onSettingsClick: () -> Unit,
    onNetworkClick: () -> Unit,
    onSandboxSetupClick: () -> Unit,
    sandboxManager: SandboxManager,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val apps by viewModel.apps.collectAsStateWithLifecycle()
    val sandboxStatus = remember { sandboxManager.getSandboxStatus() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.home_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = onNetworkClick) {
                        Icon(Icons.Default.NetworkCheck, contentDescription = stringResource(R.string.network_title))
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.nav_settings))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onInstallClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.home_install_apk))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sandbox status banner
            item {
                SandboxStatusBanner(
                    status = sandboxStatus,
                    onClick = onSandboxSetupClick
                )
            }

            if (apps.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(apps, key = { it.id }) { app ->
                    AppCard(
                        app = app,
                        onClick = { onAppClick(app.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SandboxStatusBanner(
    status: SandboxStatus,
    onClick: () -> Unit
) {
    val isActive = status == SandboxStatus.WORK_PROFILE_ACTIVE
    val bannerColor = if (isActive) RiskLow else RiskMedium

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = bannerColor.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Security,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = bannerColor
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isActive)
                        stringResource(R.string.sandbox_status_active)
                    else
                        stringResource(R.string.sandbox_status_inactive),
                    style = MaterialTheme.typography.titleSmall,
                    color = bannerColor
                )
                Text(
                    text = if (isActive)
                        stringResource(R.string.sandbox_status_active_desc)
                    else
                        stringResource(R.string.sandbox_status_inactive_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp, horizontal = 16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.home_empty_title),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.home_empty_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}
