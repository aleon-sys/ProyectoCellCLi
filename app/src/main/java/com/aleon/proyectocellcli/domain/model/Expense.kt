package com.aleon.proyectocellcli.domain.model

import java.time.LocalDate

data class Expense(
    val id: Int = 0,
    val description: String,
    val amount: Double,
    val date: LocalDate,
    val category: Category
)
