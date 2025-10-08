package com.aleon.proyectocellcli.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.aleon.proyectocellcli.data.local.entity.CategoryEntity
import com.aleon.proyectocellcli.data.local.entity.ExpenseEntity
import com.aleon.proyectocellcli.data.local.entity.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Transaction
    @Query("SELECT * FROM expenses ORDER BY dateValue DESC")
    fun getExpensesWithCategory(): Flow<List<ExpenseWithCategory>>

    @Query("SELECT * FROM expenses WHERE dateValue BETWEEN :startDate AND :endDate")
    fun getExpensesBetweenDates(startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}
