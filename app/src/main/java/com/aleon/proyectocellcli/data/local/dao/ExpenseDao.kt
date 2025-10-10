package com.aleon.proyectocellcli.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aleon.proyectocellcli.data.local.entity.CategoryEntity
import com.aleon.proyectocellcli.data.local.entity.CategoryWithTotal
import com.aleon.proyectocellcli.data.local.entity.ExpenseEntity
import com.aleon.proyectocellcli.data.local.entity.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expenses WHERE expenseId = :id")
    suspend fun deleteExpenseById(id: Long)

    @Transaction
    @Query("SELECT * FROM expenses WHERE expenseId = :id")
    fun getExpenseWithCategoryById(id: Long): Flow<ExpenseWithCategory?>

    @Transaction
    @Query("""
        SELECT c.*, SUM(e.amount) as total
        FROM categories c
        JOIN expenses e ON c.categoryId = e.expenseCategoryId
        GROUP BY c.categoryId
        ORDER BY total DESC
    """)
    fun getCategoryTotals(): Flow<List<CategoryWithTotal>>



    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY dateValue DESC")
    fun getExpensesWithCategory(): Flow<List<ExpenseWithCategory>>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}
