package com.aleon.proyectocellcli

import android.os.Build
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.test.filters.SdkSuppress
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import com.aleon.proyectocellcli.ui.navigation.Screen
import com.aleon.proyectocellcli.ui.screens.AddOutlayScreen
import com.aleon.proyectocellcli.ui.theme.ProyectocellcliTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import javax.inject.Inject

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@HiltAndroidTest
class AddOutlayScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    private lateinit var fakeExpenseRepository: com.aleon.proyectocellcli.repository.FakeExpenseRepository

    private val testCategory = Category(id = 1.toString(), name = "Ocio", color = Color.Magenta)

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeExpenseRepository = expenseRepository as com.aleon.proyectocellcli.repository.FakeExpenseRepository
        fakeExpenseRepository.insertCategories(listOf(testCategory))
    }

    @Test
    fun addMode_fillsFieldsAndSaves_newExpenseAppearsInRepository() {
        // 1. Launch the screen in "Add" mode
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                val navController = rememberNavController()
                AddOutlayScreen(navController = navController)
            }
        }

        // 2. Perform UI actions
        composeRule.onNodeWithText("Descripción").performTextInput("Café")
        composeRule.onNodeWithText("Monto").performTextInput("3.50")
        composeRule.onNodeWithText("Guardar Gasto").performClick()

        // 3. Assert that the new expense was added to the repository
        composeRule.waitUntil(timeoutMillis = 2000) {
            fakeExpenseRepository.currentExpenses.any { it.description == "Café" }
        }
        val newExpense = fakeExpenseRepository.currentExpenses.find { it.description == "Café" }
        assertEquals(3.50, newExpense?.amount)
    }

    @Test
    fun editMode_loadsDataAndSaves_updatesSuccessfully() {
        // 1. Prepare test data
        val initialExpense = Expense(id = 101, description = "Cena Tailandesa", amount = 45.0, date = LocalDate.now(), category = testCategory)
        fakeExpenseRepository.insertExpenses(listOf(initialExpense))

        // 2. Launch the screen in "Edit" mode
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.AddOutlay.route) {
                    composable(
                        route = Screen.AddOutlay.route,
                        arguments = listOf(navArgument("expenseId") {
                            type = NavType.LongType
                            defaultValue = initialExpense.id
                        })
                    ) {
                        AddOutlayScreen(navController = navController)
                    }
                }
            }
        }

        // 3. Assert initial state is loaded
        composeRule.onNodeWithText("Cena Tailandesa").assertIsDisplayed()

        // 4. Perform UI actions
        composeRule.onNodeWithText("Cena Tailandesa").performTextClearance()
        composeRule.onNodeWithText("Descripción").performTextInput("Cena Tailandesa Editada")
        composeRule.onNodeWithText("Guardar Gasto").performClick()

        // 5. Assert that the data was updated in the repository
        // We need to wait a bit for the save to process
        composeRule.waitUntil(timeoutMillis = 2000) {
            fakeExpenseRepository.currentExpenses.any { it.description == "Cena Tailandesa Editada" }
        }
        val updatedExpense = fakeExpenseRepository.currentExpenses.find { it.id == initialExpense.id }
        assertEquals("Cena Tailandesa Editada", updatedExpense?.description)
    }
}
