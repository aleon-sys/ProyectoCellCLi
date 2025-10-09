package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.use_case.GetCategoriesUseCase
import com.aleon.proyectocellcli.domain.use_case.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

// Data class to hold the final calculated data for the UI
data class CategoryTotal(
    val name: String,
    val amount: Double,
    val color: androidx.compose.ui.graphics.Color
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getExpensesUseCase: GetExpensesUseCase,
    getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    val uiState: StateFlow<List<CategoryTotal>> = combine(
        getExpensesUseCase(),
        getCategoriesUseCase()
    ) { expenses, categories ->
        val expenseMap = expenses.groupBy { it.category.id }
            .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }

        categories.map { category ->
            CategoryTotal(
                name = category.name,
                amount = expenseMap[category.id] ?: 0.0,
                color = category.color
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
