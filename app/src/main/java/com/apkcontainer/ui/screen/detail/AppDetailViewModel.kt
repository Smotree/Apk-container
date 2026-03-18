package com.apkcontainer.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkcontainer.domain.model.NetworkEvent
import com.apkcontainer.domain.model.SandboxApp
import com.apkcontainer.domain.repository.AppRepository
import com.apkcontainer.domain.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DetailUiState(
    val app: SandboxApp? = null,
    val networkEvents: List<NetworkEvent> = emptyList(),
    val isDeleted: Boolean = false
)

@HiltViewModel
class AppDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appRepository: AppRepository,
    private val networkRepository: NetworkRepository
) : ViewModel() {

    private val appId: Long = savedStateHandle["appId"] ?: 0L

    private val _state = MutableStateFlow(DetailUiState())
    val state: StateFlow<DetailUiState> = _state

    init {
        loadApp()
        loadNetworkEvents()
    }

    private fun loadApp() {
        viewModelScope.launch(Dispatchers.IO) {
            val app = appRepository.getAppById(appId)
            _state.value = _state.value.copy(app = app)
        }
    }

    private fun loadNetworkEvents() {
        viewModelScope.launch {
            networkRepository.getEventsForApp(appId).collectLatest { events ->
                _state.value = _state.value.copy(networkEvents = events)
            }
        }
    }

    fun deleteApp() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value.app?.let { app ->
                appRepository.deleteApp(app)
                networkRepository.deleteEventsForApp(app.id)
                _state.value = _state.value.copy(isDeleted = true)
            }
        }
    }
}
