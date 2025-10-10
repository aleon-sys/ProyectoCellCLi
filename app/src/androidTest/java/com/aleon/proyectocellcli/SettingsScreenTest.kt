package com.aleon.proyectocellcli

import android.os.Build
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.filters.SdkSuppress
import com.aleon.proyectocellcli.di.AppConfig
import com.aleon.proyectocellcli.di.FakeAppConfig
import com.aleon.proyectocellcli.repository.FakeUserPreferencesRepository

import com.aleon.proyectocellcli.ui.screens.SettingsScreen
import com.aleon.proyectocellcli.ui.theme.ProyectocellcliTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@HiltAndroidTest
class SettingsScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var appConfig: AppConfig

    // We don't inject this one because we are not replacing it in a Hilt module.
    // We will instantiate it manually for this test.
    private val fakeUserPreferencesRepository = FakeUserPreferencesRepository()


    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun proVersion_themeSelectorIsEnabled() {
        // 1. Setup: Ensure we are in "pro" mode
        (appConfig as FakeAppConfig).isPro = true

        // 2. Launch UI
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                SettingsScreen()
            }
        }

        // 3. Assert: Check that one of the theme radio buttons is enabled
        composeRule.onNodeWithText("Oscuro").onParent().assertIsEnabled()
    }

    @Test
    fun freeVersion_themeSelectorIsDisabled() {
        // 1. Setup: Ensure we are in "free" mode
        (appConfig as FakeAppConfig).isPro = false

        // 2. Launch UI
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                SettingsScreen()
            }
        }

        // 3. Assert: Check that one of the theme radio buttons is NOT enabled
        composeRule.onNodeWithText("Oscuro").onParent().assertIsNotEnabled()
    }

    @Test
    fun setMonthlyLimit_updatesDisplayCorrectly() {
        // 1. Launch UI
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                SettingsScreen()
            }
        }

        // 2. Assert initial state
        composeRule.onNodeWithText("No establecido").assertIsDisplayed()

        // 3. Perform actions
        composeRule.onNodeWithText("Establecer límite de gastos").performClick()
        composeRule.onNodeWithText("Límite").performTextInput("500.75")
        composeRule.onNodeWithText("Guardar").performClick()

        // 4. Assert final state
        // We need to wait for the UI to recompose with the new state
        composeRule.waitUntil(timeoutMillis = 2000) {
            composeRule.onAllNodesWithText("$500.75").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("$500.75").assertIsDisplayed()
    }
}
