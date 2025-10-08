package com.aleon.proyectocellcli.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.aleon.proyectocellcli.data.local.dao.ExpenseDao
import com.aleon.proyectocellcli.data.local.entity.CategoryEntity
import com.aleon.proyectocellcli.data.local.entity.ExpenseEntity
import com.aleon.proyectocellcli.data.local.entity.ExpenseWithCategory
import com.aleon.proyectocellcli.data.local.entity.toDomain
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val dao: ExpenseDao
) : ExpenseRepository {

    override fun getCategories(): Flow<List<Category>> {
        return dao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun addCategory(category: Category) {
        dao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(category: Category) {
        // TODO: Implement in DAO
    }

    override suspend fun updateCategory(category: Category) {
        dao.updateCategory(category.toEntity())
    }

    override suspend fun addExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }

    override suspend fun deleteExpense(expense: Expense) {
        dao.deleteExpense(expense.toEntityWithId())
    }

    override fun getExpenses(): Flow<List<Expense>> {
        return dao.getExpensesWithCategory().map { list ->
            list.map { it.toDomain() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun getExpensesForCurrentMonth(): Flow<List<Expense>> {
        val today = LocalDate.now()
        val firstDayOfMonth = today.withDayOfMonth(1)
        val lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth())

        return dao.getExpensesBetweenDates(firstDayOfMonth.toEpochDay(), lastDayOfMonth.toEpochDay()).map { list ->
            list.map {
                Expense(
                    id = it.expenseId,
                    description = it.description,
                    amount = it.amount,
                    date = LocalDate.ofEpochDay(it.dateValue),
                    category = Category(id = it.expenseCategoryId, name = "", color = Color.Transparent) // Dummy category
                )
            }
        }
    }

    override suspend fun deleteAllExpenses() {
        dao.deleteAllExpenses()
    }
}

// --- Mapper Functions ---

private fun Expense.toEntityWithId(): ExpenseEntity {
    return ExpenseEntity(
        expenseId = this.id,
        description = this.description,
        amount = this.amount,
        dateValue = this.date.toEpochDay(),
        expenseCategoryId = this.category.id
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun ExpenseWithCategory.toDomain(): Expense {
    return Expense(
        id = this.expense.expenseId,
        description = this.expense.description,
        amount = this.expense.amount,
        date = LocalDate.ofEpochDay(this.expense.dateValue),
        category = this.category.toDomain()
    )
}


private fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        categoryId = this.id,
        name = this.name,
        colorValue = this.color.toArgb() // CORRECT WAY to convert Color to Int
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        description = this.description,
        amount = this.amount,
        dateValue = this.date.toEpochDay(),
        expenseCategoryId = this.category.id
    )
}
