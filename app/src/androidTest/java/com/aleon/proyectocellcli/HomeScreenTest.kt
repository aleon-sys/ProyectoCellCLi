package com.aleon.proyectocellcli

import android.os.Build
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.filters.SdkSuppress
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import com.aleon.proyectocellcli.ui.screens.HomeScreen
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
class HomeScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var expenseRepository: ExpenseRepository

    private lateinit var fakeExpenseRepository: com.aleon.proyectocellcli.repository.FakeExpenseRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        // We need to cast the injected repository to our fake implementation to access its helper methods.
        fakeExpenseRepository = expenseRepository as com.aleon.proyectocellcli.repository.FakeExpenseRepository
    }

    @Test
    fun homeScreen_displaysCategoryTotals_correctly() {
        // 1. Prepare test data
        val foodCategory = Category(id = 1.toString()   , name = "Comida", color = Color.Red)
        val transportCategory = Category(id = 2.toString(), name = "Transporte", color = Color.Blue)

        val testExpenses = listOf(
            Expense(id = 1, description = "Tacos", amount = 15.50, date = LocalDate.now(), category = foodCategory),
            Expense(id = 2, description = "Pizza", amount = 20.00, date = LocalDate.now(), category = foodCategory),
            Expense(id = 3, description = "Bus", amount = 2.75, date = LocalDate.now(), category = transportCategory)
        )
        // Total for Food: 35.50
        // Total for Transport: 2.75

        // 2. Insert data into the fake repository
        fakeExpenseRepository.insertExpenses(testExpenses)

        // 3. Launch the UI
        composeRule.activity.setContent {
            ProyectocellcliTheme {
                HomeScreen()
            }
        }

        // 4. Assert that the data is displayed correctly
        composeRule.onNodeWithText("Comida").assertIsDisplayed()
        composeRule.onNodeWithText("$35.50").assertIsDisplayed() // Assumes USD for test

        composeRule.onNodeWithText("Transporte").assertIsDisplayed()
        composeRule.onNodeWithText("$2.75").assertIsDisplayed()

        // Also check that the total spending text is displayed
        composeRule.onNodeWithText("Gasto del periodo: $38.25").assertIsDisplayed()
    }
}
