package com.aleon.proyectocellcli.domain.use_case

import android.os.Build
import androidx.annotation.RequiresApi
import com.aleon.proyectocellcli.domain.model.CategorySpending
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class GetCategorySpendingForDateRangeUseCase @Inject constructor(
    private val repository: ExpenseRepository
) {
    operator fun invoke(startDate: LocalDate, endDate: LocalDate): Flow<List<CategorySpending>> {
        return repository.getCategorySpendingForDateRange(startDate, endDate)
    }
}
