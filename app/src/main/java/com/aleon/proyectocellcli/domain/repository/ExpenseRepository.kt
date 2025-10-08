package com.aleon.proyectocellcli.domain.repository

import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {

    fun getCategories(): Flow<List<Category>>

    suspend fun addCategory(category: Category)

    suspend fun deleteCategory(category: Category)

    suspend fun updateCategory(category: Category)

    suspend fun addExpense(expense: Expense)

    suspend fun deleteExpense(expense: Expense)

    fun getExpenses(): Flow<List<Expense>>

    fun getExpensesForCurrentMonth(): Flow<List<Expense>>

    suspend fun deleteAllExpenses()

    suspend fun getExpenseById(id: Int): Expense?

    suspend fun updateExpense(expense: Expense)
}
