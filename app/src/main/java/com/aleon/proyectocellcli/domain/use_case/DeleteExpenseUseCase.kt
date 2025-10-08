package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(expense: Expense) {
        repository.deleteExpense(expense)
    }
}
