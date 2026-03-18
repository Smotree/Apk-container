package com.apkcontainer.ui.screen.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

data class SettingsUiState(
    val vpnEnabled: Boolean = false,
    val autoAnalysis: Boolean = true,
    val themeMode: String = "system" // "light", "dark", "system"
)

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state

    fun toggleVpn() {
        _state.value = _state.value.copy(vpnEnabled = !_state.value.vpnEnabled)
    }

    fun toggleAutoAnalysis() {
        _state.value = _state.value.copy(autoAnalysis = !_state.value.autoAnalysis)
    }

    fun setTheme(mode: String) {
        _state.value = _state.value.copy(themeMode = mode)
    }
}
