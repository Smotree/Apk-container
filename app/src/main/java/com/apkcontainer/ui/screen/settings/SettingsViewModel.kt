package com.apkcontainer.ui.screen.settings

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkcontainer.service.SandboxVpnService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val vpnEnabled: Boolean = false,
    val autoAnalysis: Boolean = true,
    val themeMode: String = "system",
    val vpnError: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState(
        vpnEnabled = isVpnServiceRunning()
    ))
    val state: StateFlow<SettingsUiState> = _state

    private fun isVpnServiceRunning(): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        return am.getRunningServices(50).any {
            it.service.className == SandboxVpnService::class.java.name
        }
    }

    private fun isOtherVpnActive(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networks = cm.allNetworks
        for (network in networks) {
            val caps = cm.getNetworkCapabilities(network) ?: continue
            if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                return true
            }
        }
        return false
    }

    fun startVpn() {
        try {
            // Check if another VPN is already active
            if (isOtherVpnActive()) {
                _state.value = _state.value.copy(
                    vpnEnabled = false,
                    vpnError = "vpn_error_other_active"
                )
                return
            }

            val intent = Intent(context, SandboxVpnService::class.java).apply {
                action = SandboxVpnService.ACTION_START
            }
            context.startService(intent)
            _state.value = _state.value.copy(vpnEnabled = true, vpnError = null)
            Log.d("SettingsVM", "VPN service start requested")

            // Verify after delay that it actually started
            viewModelScope.launch {
                delay(3000)
                val running = isVpnServiceRunning()
                if (!running) {
                    _state.value = _state.value.copy(
                        vpnEnabled = false,
                        vpnError = "vpn_error_other_active"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("SettingsVM", "Failed to start VPN: ${e.message}", e)
            _state.value = _state.value.copy(vpnEnabled = false, vpnError = e.message)
        }
    }

    fun stopVpn() {
        try {
            val intent = Intent(context, SandboxVpnService::class.java).apply {
                action = SandboxVpnService.ACTION_STOP
            }
            context.startService(intent)
            _state.value = _state.value.copy(vpnEnabled = false, vpnError = null)
            Log.d("SettingsVM", "VPN service stop requested")
        } catch (e: Exception) {
            Log.e("SettingsVM", "Failed to stop VPN: ${e.message}", e)
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(vpnError = null)
    }

    fun toggleAutoAnalysis() {
        _state.value = _state.value.copy(autoAnalysis = !_state.value.autoAnalysis)
    }

    fun setTheme(mode: String) {
        _state.value = _state.value.copy(themeMode = mode)
    }
}
