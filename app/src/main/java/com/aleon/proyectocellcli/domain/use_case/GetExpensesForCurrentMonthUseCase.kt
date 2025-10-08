package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetExpensesForCurrentMonthUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(): Flow<List<Expense>> {
        return repository.getExpensesForCurrentMonth()
    }
}
