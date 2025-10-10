package com.aleon.proyectocellcli

import android.os.Build
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.filters.SdkSuppress
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import com.aleon.proyectocellcli.ui.screens.OutlayScreen
import com.aleon.proyectocellcli.ui.theme.ProyectocellcliTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import javax.inject.Inject

@SdkSuppress(minSdkVersion = Build.VERSION_CODES.O)
@HiltAndroidTest
class OutlayScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    private lateinit var fakeExpenseRepository: com.aleon.proyectocellcli.repository.FakeExpenseRepository

    private val category1 = Category(id = 1.toString(), name = "Comida", color = Color.Red)

    @Before
    fun setUp() {
        hiltRule.inject()
        fakeExpenseRepository = expenseRepository as com.aleon.proyectocellcli.repository.FakeExpenseRepository
    }

    @Test
    fun searchFunctionality_filtersCorrectly() {
        // 1. Prepare data
        val expenses = listOf(
            Expense(id = 1, description = "Café Matutino", amount = 3.50, date = LocalDate.now(), category = category1),
            Expense(id = 2, description = "Almuerzo de trabajo", amount = 12.0, date = LocalDate.now(), category = category1)
        )
        fakeExpenseRepository.insertExpenses(expenses)

        // 2. Launch UI
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                val navController = rememberNavController()
                OutlayScreen(navController = navController)
            }
        }

        // 3. Assert initial state
        composeRule.onNodeWithText("Café Matutino").assertIsDisplayed()
        composeRule.onNodeWithText("Almuerzo de trabajo").assertIsDisplayed()

        // 4. Perform search
        composeRule.onNodeWithText("Buscar gastos...").performTextInput("Café")

        // 5. Assert final state
        composeRule.onNodeWithText("Café Matutino").assertIsDisplayed()
        composeRule.onNodeWithText("Almuerzo de trabajo").assertDoesNotExist()
    }
}