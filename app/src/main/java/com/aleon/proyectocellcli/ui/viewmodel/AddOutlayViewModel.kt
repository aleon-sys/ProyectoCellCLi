package com.aleon.proyectocellcli.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
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

data class AddOutlayFormState(
    val expenseId: Int? = null,
    val description: String = "",
    val amount: String = "",
    val date: LocalDate = LocalDate.now(),
    val category: Category? = null
)

sealed class AddOutlayEvent {
    object SaveSuccess : AddOutlayEvent()
    object LimitExceeded : AddOutlayEvent()
}

data class DeleteCategoryDialogState(
    val isVisible: Boolean = false,
    val categoryToDelete: Category? = null,
    val relatedExpensesCount: Int = 0
)

@HiltViewModel
class AddOutlayViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val addExpenseUseCase: AddExpenseUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getExpenseCountForCategoryUseCase: GetExpenseCountForCategoryUseCase,
    private val getExpenseByIdUseCase: GetExpenseByIdUseCase,
    private val updateExpenseUseCase: UpdateExpenseUseCase,
    getCurrencyUseCase: GetCurrencyUseCase,
    getMonthlyLimitUseCase: GetMonthlyLimitUseCase,
    getExpensesForCurrentMonthUseCase: GetExpensesForCurrentMonthUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _formState = MutableStateFlow(AddOutlayFormState())
    val formState = _formState.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _eventFlow = MutableSharedFlow<AddOutlayEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _deleteDialogState = MutableStateFlow(DeleteCategoryDialogState())
    val deleteDialogState = _deleteDialogState.asStateFlow()
    
    val currencySymbol = getCurrencyUseCase().map { it.substringAfter("(").substringBefore(")") }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "$")

    private val totalMonthlyExpenses = MutableStateFlow(0.0)
    private val monthlyLimit = MutableStateFlow(0.0)

    init {
        loadCategories()
        
        combine(getMonthlyLimitUseCase(), getExpensesForCurrentMonthUseCase()) { limit, expenses ->
            totalMonthlyExpenses.value = expenses.sumOf { it.amount }
            monthlyLimit.value = limit
        }.launchIn(viewModelScope)

        savedStateHandle.get<Int>("expenseId")?.let { expenseId ->
            if (expenseId != -1) {
                viewModelScope.launch {
                    getExpenseByIdUseCase(expenseId)?.let { expense ->
                        _formState.value = AddOutlayFormState(
                            expenseId = expense.id,
                            description = expense.description,
                            amount = expense.amount.toString(),
                            date = expense.date,
                            category = expense.category
                        )
                    }
                }
            }
        }
    }

    private fun loadCategories() {
        getCategoriesUseCase().onEach { categoryList ->
            _categories.value = categoryList
            if (_formState.value.category == null) {
                _formState.value = _formState.value.copy(category = categoryList.firstOrNull())
            }
        }.launchIn(viewModelScope)
    }

    // --- Form State Updaters ---
    fun onDescriptionChange(newDescription: String) {
        _formState.value = _formState.value.copy(description = newDescription)
    }
    fun onAmountChange(newAmount: String) {
        _formState.value = _formState.value.copy(amount = newAmount)
    }
    fun onDateChange(newDate: LocalDate) {
        _formState.value = _formState.value.copy(date = newDate)
    }
    fun onCategoryChange(newCategory: Category) {
        _formState.value = _formState.value.copy(category = newCategory)
    }

    fun resetForm() {
        _formState.value = AddOutlayFormState(category = categories.value.firstOrNull())
    }

    // --- Business Logic ---
    fun onSaveExpense() {
        val currentState = _formState.value
        val amountDouble = currentState.amount.toDoubleOrNull()
        if (currentState.description.isBlank() || amountDouble == null || amountDouble <= 0 || currentState.category == null) {
            return
        }

        if (monthlyLimit.value > 0 && (totalMonthlyExpenses.value + amountDouble > monthlyLimit.value)) {
            viewModelScope.launch { _eventFlow.emit(AddOutlayEvent.LimitExceeded) }
        }

        val expenseToSave = Expense(
            id = currentState.expenseId ?: 0,
            description = currentState.description,
            amount = amountDouble,
            date = currentState.date,
            category = currentState.category
        )

        viewModelScope.launch {
            if (currentState.expenseId == null) {
                addExpenseUseCase(expenseToSave)
            } else {
                updateExpenseUseCase(expenseToSave)
            }
            _eventFlow.emit(AddOutlayEvent.SaveSuccess)
        }
    }
    
    fun onAddCategory(name: String, color: Color) {
        viewModelScope.launch { addCategoryUseCase(Category(name = name, color = color)) }
    }

    fun onUpdateCategory(category: Category) {
        viewModelScope.launch { updateCategoryUseCase(category) }
    }

    fun onDeleteCategory(category: Category) {
        viewModelScope.launch {
            val count = getExpenseCountForCategoryUseCase(category.id)
            _deleteDialogState.value = DeleteCategoryDialogState(
                isVisible = true,
                categoryToDelete = category,
                relatedExpensesCount = count
            )
        }
    }

    fun onConfirmDeleteCategory() {
        _deleteDialogState.value.categoryToDelete?.let { category ->
            viewModelScope.launch {
                deleteCategoryUseCase(category)
                dismissDeleteDialog()
            }
        }
    }

    fun dismissDeleteDialog() {
        _deleteDialogState.value = DeleteCategoryDialogState()
    }
}
