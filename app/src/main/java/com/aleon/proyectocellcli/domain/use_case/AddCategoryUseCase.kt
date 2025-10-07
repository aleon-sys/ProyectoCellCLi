package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import javax.inject.Inject

class AddCategoryUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(category: Category) {
        repository.addCategory(category)
    }
}
