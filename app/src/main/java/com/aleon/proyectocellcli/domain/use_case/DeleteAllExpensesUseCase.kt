package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteAllExpensesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke() {
        repository.deleteAllExpenses()
    }
}
