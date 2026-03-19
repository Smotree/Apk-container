package com.apkcontainer.ui.screen.analysis

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkcontainer.data.apk.ApkAnalysisResult
import com.apkcontainer.data.apk.ApkAnalyzer
import com.apkcontainer.domain.repository.AppRepository
import com.apkcontainer.sandbox.VirtualContainer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AnalysisUiState(
    val isLoading: Boolean = true,
    val result: ApkAnalysisResult? = null,
    val error: String? = null,
    val isInstalling: Boolean = false,
    val installedAppId: Long? = null,
    val installIntent: Intent? = null
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val apkAnalyzer: ApkAnalyzer,
    private val appRepository: AppRepository,
    private val virtualContainer: VirtualContainer
) : ViewModel() {

    private val _state = MutableStateFlow(AnalysisUiState())
    val state: StateFlow<AnalysisUiState> = _state

    fun analyze(apkPath: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = AnalysisUiState(isLoading = true)
            try {
                val result = apkAnalyzer.analyzeApk(apkPath)
                if (result != null) {
                    _state.value = AnalysisUiState(isLoading = false, result = result)
                } else {
                    _state.value = AnalysisUiState(isLoading = false, error = "Не удалось проанализировать APK")
                }
            } catch (e: Exception) {
                _state.value = AnalysisUiState(isLoading = false, error = e.message)
            }
        }
    }

    fun installInSandbox() {
        val result = _state.value.result ?: return
        _state.value = _state.value.copy(isInstalling = true)

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Store APK in sandbox private directory
                val storedApk = virtualContainer.storeApk(
                    result.app.apkPath,
                    result.app.packageName
                )

                // Save to database
                val appId = appRepository.insertApp(
                    result.app.copy(isInstalledInSandbox = true)
                )

                // Create install intent
                val intent = if (storedApk != null) {
                    virtualContainer.createInstallIntent(storedApk)
                } else {
                    null
                }

                _state.value = _state.value.copy(
                    isInstalling = false,
                    installedAppId = appId,
                    installIntent = intent
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isInstalling = false,
                    error = e.message
                )
            }
        }
    }

    fun onInstallIntentConsumed() {
        _state.value = _state.value.copy(installIntent = null)
    }
}
