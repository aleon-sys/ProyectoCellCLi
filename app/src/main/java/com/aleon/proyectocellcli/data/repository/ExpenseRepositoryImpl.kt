package com.aleon.proyectocellcli.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.toArgb
import com.aleon.proyectocellcli.data.local.dao.ExpenseDao
import com.aleon.proyectocellcli.data.local.entity.CategoryEntity
import com.aleon.proyectocellcli.data.local.entity.ExpenseEntity
import com.aleon.proyectocellcli.data.local.entity.toDomain
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun addExpense(expense: Expense) {
        dao.insertExpense(expense.toEntity())
    }
}

// --- Mapper Functions ---

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
