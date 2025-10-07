package com.aleon.proyectocellcli.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ExpenseWithCategory(
    @Embedded val expense: ExpenseEntity,
    @Relation(
        parentColumn = "expenseCategoryId",
        entityColumn = "categoryId"
    )
    val category: CategoryEntity
)
