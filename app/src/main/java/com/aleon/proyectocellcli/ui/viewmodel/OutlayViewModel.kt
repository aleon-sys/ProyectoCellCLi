package com.aleon.proyectocellcli.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.use_case.GetExpensesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class OutlayViewModel @Inject constructor(
    private val getExpensesUseCase: GetExpensesUseCase
) : ViewModel() {

    private val _expensesByDate = MutableStateFlow<Map<LocalDate, List<Expense>>>(emptyMap())
    val expensesByDate = _expensesByDate.asStateFlow()

    init {
        loadExpenses()
    }

    private fun loadExpenses() {
        getExpensesUseCase().onEach { expenses ->
            _expensesByDate.value = expenses.groupBy { it.date }
        }.launchIn(viewModelScope)
    }
}
