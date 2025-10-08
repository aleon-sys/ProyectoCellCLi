package com.aleon.proyectocellcli.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.Category
import com.aleon.proyectocellcli.domain.model.Expense
import com.aleon.proyectocellcli.domain.use_case.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AddOutlayUiState(
    val totalMonthlyExpenses: Double = 0.0,
    val monthlyLimit: Double = 0.0
)

sealed class AddOutlayEvent {
    object SaveSuccess : AddOutlayEvent()
    object LimitExceeded : AddOutlayEvent()
}

@HiltViewModel
class AddOutlayViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    getCurrencyUseCase: GetCurrencyUseCase,
    getMonthlyLimitUseCase: GetMonthlyLimitUseCase,
    getExpensesForCurrentMonthUseCase: GetExpensesForCurrentMonthUseCase
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _uiState = MutableStateFlow(AddOutlayUiState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddOutlayEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    val currencySymbol = getCurrencyUseCase().map { currencyString ->
        currencyString.substringAfter("(").substringBefore(")")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "$"
    )

    init {
        loadCategories()

        combine(
            getMonthlyLimitUseCase(),
            getExpensesForCurrentMonthUseCase()
        ) { limit, expenses ->
            _uiState.value = AddOutlayUiState(
                totalMonthlyExpenses = expenses.sumOf { it.amount },
                monthlyLimit = limit
            )
        }.launchIn(viewModelScope)
    }

    private fun loadCategories() {
        getCategoriesUseCase().onEach { categoryList ->
            _categories.value = categoryList
        }.launchIn(viewModelScope)
    }

    fun onAddCategory(name: String, color: Color) {
        viewModelScope.launch {
            addCategoryUseCase(Category(name = name, color = color))
        }
    }

    fun onUpdateCategory(category: Category) {
        viewModelScope.launch {
            updateCategoryUseCase(category)
        }
    }

    fun onSaveExpense(description: String, amount: String, category: Category, date: LocalDate) {
        val amountDouble = amount.toDoubleOrNull()
        if (description.isBlank() || amountDouble == null || amountDouble <= 0) {
            // TODO: Handle validation error
            return
        }

        val currentState = _uiState.value
        if (currentState.monthlyLimit > 0 && (currentState.totalMonthlyExpenses + amountDouble > currentState.monthlyLimit)) {
            viewModelScope.launch {
                _eventFlow.emit(AddOutlayEvent.LimitExceeded)
            }
        }

        viewModelScope.launch {
            addExpenseUseCase(
                Expense(
                    description = description,
                    amount = amountDouble,
                    date = date,
                    category = category
                )
            )
            _eventFlow.emit(AddOutlayEvent.SaveSuccess)
        }
    }
}
