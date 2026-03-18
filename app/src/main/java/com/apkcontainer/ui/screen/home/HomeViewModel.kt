package com.apkcontainer.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apkcontainer.domain.model.SandboxApp
import com.apkcontainer.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    appRepository: AppRepository
) : ViewModel() {

    val apps: StateFlow<List<SandboxApp>> = appRepository.getAllApps()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
