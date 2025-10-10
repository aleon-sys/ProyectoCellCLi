package com.aleon.proyectocellcli.repository

import com.aleon.proyectocellcli.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// A fake repository for user preferences used in UI tests.
@Singleton
class FakeUserPreferencesRepository @Inject constructor() {

    private val _theme = MutableStateFlow("Claro")
    val theme: Flow<String> = _theme

    private val _currency = MutableStateFlow("USD ($)")
    val currency: Flow<String> = _currency

    private val _monthlyLimit = MutableStateFlow(0f)
    val monthlyLimit: Flow<Float> = _monthlyLimit

    suspend fun setTheme(theme: String) {
        _theme.value = theme
    }

    suspend fun setCurrency(currency: String) {
        _currency.value = currency
    }

    suspend fun setMonthlyLimit(limit: Float) {
        _monthlyLimit.value = limit
    }
}
