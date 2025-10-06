package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {
    private val _message = MutableStateFlow("Hello Android from ViewModel!")
    val message: StateFlow<String> = _message.asStateFlow()
}
