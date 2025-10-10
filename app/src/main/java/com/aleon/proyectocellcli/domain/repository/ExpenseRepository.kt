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

    suspend fun updateExpense(expense: Expense)

    suspend fun deleteExpenseById(id: Long)

    fun getExpenseById(id: Long): Flow<Expense?>


    fun getExpenses(): Flow<List<Expense>>

    suspend fun deleteAllExpenses()
}
