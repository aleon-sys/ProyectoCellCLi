package com.aleon.proyectocellcli

import android.os.Build
import androidx.activity.compose.setContent
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
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

    private val fakeUserPreferencesRepository = FakeUserPreferencesRepository()


    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun proVersion_themeSelectorIsEnabled() {
        (appConfig as FakeAppConfig).isPro = true

        composeRule.activity.setContent {
            ProyectocellcliTheme {
                SettingsScreen()
            }
        }

        composeRule.onNodeWithText("Oscuro").onParent().assertIsEnabled()
    }

    @Test
    fun freeVersion_themeSelectorIsDisabled() {
        (appConfig as FakeAppConfig).isPro = false

        composeRule.activity.setContent {
            ProyectocellcliTheme {
                SettingsScreen()
            }
        }

        composeRule.onNodeWithText("Oscuro").onParent().assertIsNotEnabled()
    }

    @Test
    fun setMonthlyLimit_updatesDisplayCorrectly() {
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                SettingsScreen()
            }
        }

        composeRule.onNodeWithText("No establecido").assertIsDisplayed()

        composeRule.onNodeWithText("Establecer límite de gastos").performClick()
        composeRule.onNodeWithText("Límite").performTextInput("500.75")
        composeRule.onNodeWithText("Guardar").performClick()

        composeRule.waitUntil(timeoutMillis = 2000) {
            composeRule.onAllNodesWithText("$500.75").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithText("$500.75").assertIsDisplayed()
    }
}
