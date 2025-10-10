package com.aleon.proyectocellcli.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.di.AppConfig
import com.aleon.proyectocellcli.domain.use_case.GetCurrencyUseCase
import com.aleon.proyectocellcli.domain.use_case.GetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase,
    getCurrencyUseCase: GetCurrencyUseCase,
    appConfig: AppConfig
) : ViewModel() {

    val theme: StateFlow<String> = getThemeUseCase().map { theme ->
        if (appConfig.isPro) {
            theme
        } else {
            "Claro" // Force light theme for free version
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Claro"
    )

    val currency = getCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "USD ($)"
    )
}