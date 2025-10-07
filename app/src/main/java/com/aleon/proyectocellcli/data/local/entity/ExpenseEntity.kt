package com.aleon.proyectocellcli.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["expenseCategoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val expenseId: Int = 0,
    val description: String,
    val amount: Double,
    val dateValue: Long, // Storing date as a primitive Long
    @ColumnInfo(index = true) val expenseCategoryId: String
)
