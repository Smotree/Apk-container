package com.apkcontainer.ui.screen.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkcontainer.data.apk.ApkAnalysisResult
import com.apkcontainer.data.apk.ApkAnalyzer
import com.apkcontainer.domain.repository.AppRepository
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
    val installedAppId: Long? = null
)

@HiltViewModel
class AnalysisViewModel @Inject constructor(
    private val apkAnalyzer: ApkAnalyzer,
    private val appRepository: AppRepository
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
                val appId = appRepository.insertApp(
                    result.app.copy(isInstalledInSandbox = true)
                )
                _state.value = _state.value.copy(
                    isInstalling = false,
                    installedAppId = appId
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isInstalling = false,
                    error = e.message
                )
            }
        }
    }
}
