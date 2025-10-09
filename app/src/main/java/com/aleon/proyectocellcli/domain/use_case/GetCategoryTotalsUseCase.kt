package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.model.CategoryTotal
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate
import javax.inject.Inject

class GetCategoryTotalsUseCase @Inject constructor(
    private val expenseRepository: ExpenseRepository,
    private val getCategoriesUseCase: GetCategoriesUseCase
) {
    operator fun invoke(
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CategoryTotal>> {
        
        val allExpensesFlow = expenseRepository.getExpenses()
        val allCategoriesFlow = getCategoriesUseCase()

        return combine(allExpensesFlow, allCategoriesFlow) { expenses, categories ->
            // First, filter expenses by the selected date range
            val filteredExpenses = expenses.filter {
                !it.date.isBefore(startDate) && !it.date.isAfter(endDate)
            }

            // Then, group the filtered expenses by category and sum their amounts
            val totalsByCategory = filteredExpenses
                .groupBy { it.category.id }
                .mapValues { entry -> entry.value.sumOf { it.amount } }

            // Finally, create the list of CategoryTotal, ensuring all categories are present
            categories.map { category ->
                CategoryTotal(
                    category = category,
                    total = totalsByCategory[category.id] ?: 0.0
                )
            }
        }
    }
}
