package com.aleon.proyectocellcli.data.local.entity

import androidx.room.Embedded

data class CategoryWithTotal(
    @Embedded
    val category: CategoryEntity,
    val total: Double
)
