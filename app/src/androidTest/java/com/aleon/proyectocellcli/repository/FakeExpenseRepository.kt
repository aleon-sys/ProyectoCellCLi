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

@RequiresApi(Build.VERSION_CODES.O)
@Singleton
class FakeExpenseRepository @Inject constructor() : ExpenseRepository {

    private val _expenses = MutableStateFlow<List<Expense>>(emptyList())
    private val _categories = MutableStateFlow<List<Category>>(emptyList())

    val currentExpenses: List<Expense> get() = _expenses.value

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

    override fun getCategorySpendingForDateRange(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CategorySpending>> {
        return _expenses.map { expenses ->
            expenses
                .filter { it.date in startDate..endDate }
                .groupBy { it.category }
                .map { (category, expenseList) ->
                    CategorySpending(category, expenseList.sumOf { it.amount })
                }
        }
    }

    override suspend fun addExpense(expense: Expense) {
        _expenses.value = _expenses.value.plus(expense)
    }

    override suspend fun updateExpense(expense: Expense) {
        val currentList = _expenses.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == expense.id }
        if (index != -1) {
            currentList[index] = expense
            _expenses.value = currentList
        }
    }

    override fun getExpenseById(id: Long): Flow<Expense?> {
        return _expenses.map { list -> list.find { it.id.toLong() == id } }
    }

    override suspend fun addCategory(category: Category) {
        _categories.value = _categories.value.plus(category)
    }

    override suspend fun deleteCategory(category: Category) {}

    override suspend fun updateCategory(category: Category) {}

    override suspend fun deleteExpenseById(id: Long) {
        _expenses.value = _expenses.value.filterNot { it.id.toLong() == id }
    }

    override fun getExpenses(): Flow<List<Expense>> = _expenses.asStateFlow()

    override suspend fun deleteAllExpenses() {
        _expenses.value = emptyList()
    }

    fun insertExpenses(expenses: List<Expense>) {
        _expenses.value = expenses
    }

    fun insertCategories(categories: List<Category>) {
        _categories.value = categories
    }
}