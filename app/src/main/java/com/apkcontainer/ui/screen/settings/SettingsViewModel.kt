package com.apkcontainer.ui.screen.settings

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.util.Log
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

    private val _state = MutableStateFlow(SettingsUiState(
        vpnEnabled = isVpnRunning()
    ))
    val state: StateFlow<SettingsUiState> = _state

    private fun isVpnRunning(): Boolean {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        return am.getRunningServices(50).any {
            it.service.className == SandboxVpnService::class.java.name
        }
    }

    fun getVpnPermissionIntent(): Intent? {
        return VpnService.prepare(context)
    }

    fun startVpn() {
        try {
            val intent = Intent(context, SandboxVpnService::class.java).apply {
                action = SandboxVpnService.ACTION_START
            }
            context.startService(intent)
            _state.value = _state.value.copy(vpnEnabled = true)
            Log.d("SettingsVM", "VPN service start requested")
        } catch (e: Exception) {
            Log.e("SettingsVM", "Failed to start VPN: ${e.message}", e)
        }
    }

    fun stopVpn() {
        try {
            val intent = Intent(context, SandboxVpnService::class.java).apply {
                action = SandboxVpnService.ACTION_STOP
            }
            context.startService(intent)
            _state.value = _state.value.copy(vpnEnabled = false)
            Log.d("SettingsVM", "VPN service stop requested")
        } catch (e: Exception) {
            Log.e("SettingsVM", "Failed to stop VPN: ${e.message}", e)
        }
    }

    fun toggleAutoAnalysis() {
        _state.value = _state.value.copy(autoAnalysis = !_state.value.autoAnalysis)
    }

    fun setTheme(mode: String) {
        _state.value = _state.value.copy(themeMode = mode)
    }
}
