package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import javax.inject.Inject

class GetExpenseCountForCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(categoryId: String): Int {
        return repository.getExpenseCountForCategory(categoryId)
    }
}