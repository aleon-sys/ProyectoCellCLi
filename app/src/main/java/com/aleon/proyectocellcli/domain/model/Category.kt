package com.aleon.proyectocellcli.domain.model

import androidx.compose.ui.graphics.Color
import java.util.UUID

data class Category(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Color
)
