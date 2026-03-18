package com.apkcontainer.ui.screen.install

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class InstallViewModel @Inject constructor() : ViewModel() {

    private val _selectedUri = MutableStateFlow<Uri?>(null)
    val selectedUri: StateFlow<Uri?> = _selectedUri

    fun onFileSelected(uri: Uri) {
        _selectedUri.value = uri
    }
}
