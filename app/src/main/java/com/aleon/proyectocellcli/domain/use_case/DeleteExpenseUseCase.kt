package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import javax.inject.Inject

class DeleteExpenseUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteExpenseById(id)
    }
}
