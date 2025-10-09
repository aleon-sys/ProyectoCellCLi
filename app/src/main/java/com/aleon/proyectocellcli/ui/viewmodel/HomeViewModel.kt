package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.use_case.GetCategoriesUseCase
import com.aleon.proyectocellcli.domain.use_case.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
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

    // StateFlow to hold the currently selected date for filtering
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // The final UI state, which reacts to changes in expenses, categories, AND the selected date
    val uiState: StateFlow<List<CategoryTotal>> = combine(
        getExpensesUseCase(),
        getCategoriesUseCase(),
        _selectedDate
    ) { expenses, categories, date ->
        // Filter expenses for the selected date first
        val filteredExpenses = expenses.filter { it.date == date }

        // Group filtered expenses by category ID and sum their amounts
        val expenseMap = filteredExpenses.groupBy { it.category.id }
            .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }

        // Create the final list, including all categories
        // We can decide if we want to show categories with 0 expense for that day
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

    // Function to be called from the UI when the user selects a new date
    fun onDateSelected(newDate: LocalDate) {
        _selectedDate.value = newDate
    }
}