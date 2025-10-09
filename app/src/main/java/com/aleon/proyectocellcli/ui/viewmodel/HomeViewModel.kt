package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.use_case.GetCategoriesUseCase
import com.aleon.proyectocellcli.domain.use_case.GetCurrencyUseCase
import com.aleon.proyectocellcli.domain.use_case.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.Month
import javax.inject.Inject

// Data class to hold the final calculated data for the UI
data class CategoryTotal(
    val name: String,
    val amount: Double,
    val color: androidx.compose.ui.graphics.Color
)

// New unified UI state class
data class HomeScreenState(
    val categoryTotals: List<CategoryTotal> = emptyList(),
    val currencySymbol: String = "$"
)

// Enum to represent the type of filter active
enum class Timeframe { DAY, MONTH, YEAR, PERIOD }

// Data class to hold the complete filter state
data class DateFilterState(
    val timeframe: Timeframe = Timeframe.MONTH,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    getExpensesUseCase: GetExpensesUseCase,
    getCategoriesUseCase: GetCategoriesUseCase,
    getCurrencyUseCase: GetCurrencyUseCase
) : ViewModel() {

    private val _dateFilterState = MutableStateFlow(DateFilterState())
    val dateFilterState: StateFlow<DateFilterState> = _dateFilterState.asStateFlow()

    val uiState: StateFlow<HomeScreenState> = combine(
        getExpensesUseCase(),
        getCategoriesUseCase(),
        _dateFilterState,
        getCurrencyUseCase()
    ) { expenses, categories, filter, currencyPref ->
        val filteredExpenses = when (filter.timeframe) {
            Timeframe.DAY -> expenses.filter { it.date == filter.startDate }
            Timeframe.MONTH -> expenses.filter { it.date.year == filter.startDate.year && it.date.month == filter.startDate.month }
            Timeframe.YEAR -> expenses.filter { it.date.year == filter.startDate.year }
            Timeframe.PERIOD -> expenses.filter { expense ->
                !expense.date.isBefore(filter.startDate) && !expense.date.isAfter(filter.endDate!!)
            }
        }

        val expenseMap = filteredExpenses.groupBy { it.category.id }
            .mapValues { (_, expenseList) -> expenseList.sumOf { it.amount } }

        val categoryTotals = categories.map { category ->
            CategoryTotal(
                name = category.name,
                amount = expenseMap[category.id] ?: 0.0,
                color = category.color
            )
        }
        
        val currencySymbol = currencyPref.substringAfter("(").substringBefore(")")
        
        HomeScreenState(categoryTotals = categoryTotals, currencySymbol = currencySymbol)

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeScreenState()
    )

    fun onDateSelected(date: LocalDate) {
        _dateFilterState.value = DateFilterState(timeframe = Timeframe.DAY, startDate = date)
    }

    fun onMonthSelected(month: Month, year: Int) {
        _dateFilterState.value = DateFilterState(timeframe = Timeframe.MONTH, startDate = LocalDate.of(year, month, 1))
    }

    fun onYearSelected(year: Int) {
        _dateFilterState.value = DateFilterState(timeframe = Timeframe.YEAR, startDate = LocalDate.of(year, 1, 1))
    }

    fun onPeriodSelected(startDate: LocalDate, endDate: LocalDate) {
        _dateFilterState.value = DateFilterState(timeframe = Timeframe.PERIOD, startDate = startDate, endDate = endDate)
    }
}