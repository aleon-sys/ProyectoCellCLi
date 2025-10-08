package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.use_case.GetCurrencyUseCase
import com.aleon.proyectocellcli.domain.use_case.GetThemeUseCase
import com.aleon.proyectocellcli.domain.use_case.SetCurrencyUseCase
import com.aleon.proyectocellcli.domain.use_case.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase,
    private val getCurrencyUseCase: GetCurrencyUseCase,
    private val setCurrencyUseCase: SetCurrencyUseCase
) : ViewModel() {

    val theme = getThemeUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Sistema"
    )

    val currency = getCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "USD ($)"
    )

    fun onThemeSelected(theme: String) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }

    fun onCurrencySelected(currency: String) {
        viewModelScope.launch {
            setCurrencyUseCase(currency)
        }
    }
}
