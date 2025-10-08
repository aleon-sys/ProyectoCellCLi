package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.use_case.DeleteExpenseUseCase
import com.aleon.proyectocellcli.domain.use_case.GetCurrencyUseCase
import com.aleon.proyectocellcli.domain.use_case.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    getCurrencyUseCase: GetCurrencyUseCase
) : ViewModel() {

    private val _expensesByDate = MutableStateFlow<Map<LocalDate, List<Expense>>>(emptyMap())
    val expensesByDate = _expensesByDate.asStateFlow()

    val currencySymbol = getCurrencyUseCase().map { currencyString ->
        currencyString.substringAfter("(").substringBefore(")")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "$"
    )

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        getExpensesUseCase().onEach { expenses ->
            _expensesByDate.value = expenses.groupBy { it.date }
        }.launchIn(viewModelScope)
    }

    fun onDeleteExpense(expense: Expense) {
        viewModelScope.launch {
            deleteExpenseUseCase(expense)
        }
    }
}
