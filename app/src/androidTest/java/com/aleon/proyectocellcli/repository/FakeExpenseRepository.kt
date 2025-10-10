package com.aleon.proyectocellcli.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.CategorySpending
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

import javax.inject.Inject
import javax.inject.Singleton

// A fake repository that holds data in memory for UI tests.
@RequiresApi(Build.VERSION_CODES.O)
@Singleton // Ensure only one instance of the fake is used throughout the test
class FakeExpenseRepository @Inject constructor() : ExpenseRepository {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())

    override fun getCategories(): Flow<List<Category>> = _categories.asStateFlow()

    override fun getAllCategorySpending(): Flow<List<CategorySpending>> {
        return _expenses.map { expenses ->
            expenses
                .groupBy { it.category }
                .map { (category, expenseList) ->
                    CategorySpending(category, expenseList.sumOf { it.amount })
                }
        }
    }

    override fun getCategorySpendingForDateRange(startDate: LocalDate, endDate: LocalDate): Flow<List<CategorySpending>> {
        return _expenses.map { expenses ->
            expenses
                .filter { it.date in startDate..endDate }
                .groupBy { it.category }
                .map { (category, expenseList) ->
                    CategorySpending(category, expenseList.sumOf { it.amount })
                }
        }
    }

    // --- Unused functions for HomeScreen test, but required by the interface ---
    override suspend fun addCategory(category: Category) { /* Not needed for this test */ }
    override suspend fun deleteCategory(category: Category) { /* Not needed for this test */ }
    override suspend fun updateCategory(category: Category) { /* Not needed for this test */ }
    override suspend fun addExpense(expense: Expense) { /* Not needed for this test */ }
    override suspend fun updateExpense(expense: Expense) { /* Not needed for this test */ }
    override suspend fun deleteExpenseById(id: Long) { /* Not needed for this test */ }
    override fun getExpenseById(id: Long): Flow<Expense?> { TODO("Not yet implemented") }
    override fun getExpenses(): Flow<List<Expense>> = _expenses.asStateFlow()
    override suspend fun deleteAllExpenses() { /* Not needed for this test */ }

    // --- Helper function for tests to insert data ---
    fun insertExpenses(expenses: List<Expense>) {
        _expenses.value = expenses
    }
    fun insertCategories(categories: List<Category>) {
        _categories.value = categories
    }
}
