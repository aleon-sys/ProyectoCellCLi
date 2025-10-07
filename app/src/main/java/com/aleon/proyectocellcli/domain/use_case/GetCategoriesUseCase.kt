package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return repository.getCategories()
    }
}
