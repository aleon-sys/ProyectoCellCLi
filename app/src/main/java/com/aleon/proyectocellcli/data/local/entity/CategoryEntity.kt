package com.aleon.proyectocellcli.data.local.entity

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aleon.proyectocellcli.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val categoryId: String,
    val name: String,
    val colorValue: Int // Storing color as a primitive Int
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = this.categoryId,
        name = this.name,
        color = Color(this.colorValue) // Conversion happens here, at the layer boundary
    )
}
