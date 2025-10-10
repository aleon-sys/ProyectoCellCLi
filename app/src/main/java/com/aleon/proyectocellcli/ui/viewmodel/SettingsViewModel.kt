package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.di.AppConfig
import com.aleon.proyectocellcli.domain.use_case.DeleteAllExpensesUseCase
import com.aleon.proyectocellcli.domain.use_case.GetCurrencyUseCase
import com.aleon.proyectocellcli.domain.use_case.GetMonthlyLimitUseCase
import com.aleon.proyectocellcli.domain.use_case.GetThemeUseCase
import com.aleon.proyectocellcli.domain.use_case.SetCurrencyUseCase
import com.aleon.proyectocellcli.domain.use_case.SetMonthlyLimitUseCase
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
    private val setCurrencyUseCase: SetCurrencyUseCase,
    private val getMonthlyLimitUseCase: GetMonthlyLimitUseCase,
    private val setMonthlyLimitUseCase: SetMonthlyLimitUseCase,
    private val deleteAllExpensesUseCase: DeleteAllExpensesUseCase,
    appConfig: AppConfig
) : ViewModel() {

    val isProVersion = appConfig.isPro

    val theme = getThemeUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Claro"
    )

    val currency = getCurrencyUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "USD ($)"
    )

    val monthlyLimit = getMonthlyLimitUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
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

    fun onSetMonthlyLimit(limit: String) {
        viewModelScope.launch {
            val limitAsFloat = limit.toFloatOrNull() ?: 0f
            setMonthlyLimitUseCase(limitAsFloat)
        }
    }

    fun onDeleteAllExpenses() {
        viewModelScope.launch {
            deleteAllExpensesUseCase()
        }
    }
}
