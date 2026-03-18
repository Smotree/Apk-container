package com.apkcontainer.ui.screen.network

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkcontainer.domain.model.NetworkEvent
import com.apkcontainer.domain.repository.NetworkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

enum class NetworkFilter { ALL, HTTP, HTTPS, DNS }

@HiltViewModel
class NetworkViewModel @Inject constructor(
    networkRepository: NetworkRepository
) : ViewModel() {

    private val _filter = MutableStateFlow(NetworkFilter.ALL)
    val filter: StateFlow<NetworkFilter> = _filter

    val events: StateFlow<List<NetworkEvent>> = combine(
        networkRepository.getAllEvents(),
        _filter
    ) { events, filter ->
        when (filter) {
            NetworkFilter.ALL -> events
            NetworkFilter.HTTP -> events.filter { it.remotePort == 80 }
            NetworkFilter.HTTPS -> events.filter { it.remotePort == 443 }
            NetworkFilter.DNS -> events.filter { it.remotePort == 53 || it.protocol == "DNS" }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: NetworkFilter) {
        _filter.value = filter
    }
}
