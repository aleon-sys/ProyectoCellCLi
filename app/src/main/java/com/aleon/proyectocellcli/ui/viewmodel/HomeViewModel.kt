package com.aleon.proyectocellcli.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aleon.proyectocellcli.domain.model.CategorySpending
import com.aleon.proyectocellcli.domain.use_case.GetAllCategorySpendingUseCase
import com.aleon.proyectocellcli.domain.use_case.GetCategorySpendingForDateRangeUseCase
import com.aleon.proyectocellcli.domain.use_case.GetMonthlyLimitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import javax.inject.Inject

data class DateFilter(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val description: String
)

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCategorySpendingForDateRangeUseCase: GetCategorySpendingForDateRangeUseCase,
    getMonthlyLimitUseCase: GetMonthlyLimitUseCase
) : ViewModel() {

    private val _dateFilter = MutableStateFlow<DateFilter?>(null)

    val filterDescription: StateFlow<String> = _dateFilter.map { filter ->
        filter?.description ?: "Este Mes"
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = "Este Mes"
    )

    val categorySpending: StateFlow<List<CategorySpending>> = _dateFilter.flatMapLatest { filter ->
        val dateRange = filter ?: run {
            val today = LocalDate.now()
            val startDate = today.with(TemporalAdjusters.firstDayOfMonth())
            val endDate = today.with(TemporalAdjusters.lastDayOfMonth())
            DateFilter(startDate, endDate, "Este Mes")
        }
        getCategorySpendingForDateRangeUseCase(dateRange.startDate, dateRange.endDate)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val totalSpending = categorySpending.map { list ->
        list.sumOf { it.total }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0.0
    )

    val monthlyLimit = getMonthlyLimitUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    private val dayFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    private val periodFormatter = DateTimeFormatter.ofPattern("dd/MM/yy", Locale("es", "ES"))

    fun setDayFilter(date: LocalDate) {
        val description = date.format(dayFormatter)
        _dateFilter.value = DateFilter(date, date, description)
    }

    fun setMonthFilter(month: Month, year: Int) {
        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
        val description = "${month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() }} $year"
        _dateFilter.value = DateFilter(startDate, endDate, description)
    }

    fun setYearFilter(year: Int) {
        val startDate = LocalDate.of(year, Month.JANUARY, 1)
        val endDate = LocalDate.of(year, Month.DECEMBER, 31)
        _dateFilter.value = DateFilter(startDate, endDate, year.toString())
    }

    fun setPeriodFilter(startDate: LocalDate, endDate: LocalDate) {
        val description = "${startDate.format(periodFormatter)} - ${endDate.format(periodFormatter)}"
        _dateFilter.value = DateFilter(startDate, endDate, description)
    }
}