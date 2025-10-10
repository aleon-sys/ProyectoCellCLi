package com.aleon.proyectocellcli

import android.os.Build
import androidx.activity.compose.setContent
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
        fakeExpenseRepository = expenseRepository as com.aleon.proyectocellcli.repository.FakeExpenseRepository
    }

    @Test
    fun homeScreen_displaysCategoryTotals_correctly() {
        val foodCategory = Category(id = 1.toString()   , name = "Comida", color = Color.Red)
        val transportCategory = Category(id = 2.toString(), name = "Transporte", color = Color.Blue)

        val testExpenses = listOf(
            Expense(id = 1, description = "Tacos", amount = 15.50, date = LocalDate.now(), category = foodCategory),
            Expense(id = 2, description = "Pizza", amount = 20.00, date = LocalDate.now(), category = foodCategory),
            Expense(id = 3, description = "Bus", amount = 2.75, date = LocalDate.now(), category = transportCategory)
        )
        fakeExpenseRepository.insertExpenses(testExpenses)

        composeRule.activity.setContent {
            ProyectocellcliTheme {
                HomeScreen()
            }
        }

        composeRule.onNodeWithText("Comida").assertIsDisplayed()
        composeRule.onNodeWithText("$35.50").assertIsDisplayed()

        composeRule.onNodeWithText("Transporte").assertIsDisplayed()
        composeRule.onNodeWithText("$2.75").assertIsDisplayed()

        composeRule.onNodeWithText("Gasto del periodo: $38.25").assertIsDisplayed()
    }
}
