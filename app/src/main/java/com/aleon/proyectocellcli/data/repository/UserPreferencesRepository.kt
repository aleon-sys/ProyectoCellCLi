package com.aleon.proyectocellcli.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class UserPreferencesRepository @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val THEME = stringPreferencesKey("theme_preference")
        val CURRENCY = stringPreferencesKey("currency_preference")
        val MONTHLY_LIMIT = floatPreferencesKey("monthly_limit")
    }

    val theme = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME] ?: "Claro"
    }

    val currency = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.CURRENCY] ?: "USD ($)"
    }

    val monthlyLimit = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.MONTHLY_LIMIT] ?: 0f
    }

    suspend fun setTheme(theme: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = theme
        }
    }

    suspend fun setCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.CURRENCY] = currency
        }
    }

    suspend fun setMonthlyLimit(limit: Float) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MONTHLY_LIMIT] = limit
        }
    }
}
