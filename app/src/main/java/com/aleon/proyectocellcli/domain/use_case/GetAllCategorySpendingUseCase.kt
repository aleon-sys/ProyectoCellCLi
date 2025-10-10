package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.model.CategorySpending
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllCategorySpendingUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<CategorySpending>> {
        return repository.getAllCategorySpending()
    }
}
