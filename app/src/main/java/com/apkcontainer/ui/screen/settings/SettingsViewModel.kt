package com.apkcontainer.ui.screen.settings

import android.content.Context
import android.content.Intent
import android.net.VpnService
import androidx.lifecycle.ViewModel
import com.apkcontainer.service.SandboxVpnService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SettingsUiState(
    val vpnEnabled: Boolean = false,
    val autoAnalysis: Boolean = true,
    val themeMode: String = "system"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    /**
     * Returns VPN permission intent if needed, null if already granted.
     */
    fun getVpnPermissionIntent(): Intent? {
        return VpnService.prepare(context)
    }

    fun startVpn() {
        val intent = Intent(context, SandboxVpnService::class.java).apply {
            action = SandboxVpnService.ACTION_START
        }
        context.startForegroundService(intent)
        _state.value = _state.value.copy(vpnEnabled = true)
    }

    fun stopVpn() {
        val intent = Intent(context, SandboxVpnService::class.java).apply {
            action = SandboxVpnService.ACTION_STOP
        }
        context.startService(intent)
        _state.value = _state.value.copy(vpnEnabled = false)
    }

    fun toggleVpn() {
        if (_state.value.vpnEnabled) {
            stopVpn()
        }
        // If enabling, caller must check VPN permission first
    }

    fun toggleAutoAnalysis() {
        _state.value = _state.value.copy(autoAnalysis = !_state.value.autoAnalysis)
    }

    fun setTheme(mode: String) {
        _state.value = _state.value.copy(themeMode = mode)
    }
}
