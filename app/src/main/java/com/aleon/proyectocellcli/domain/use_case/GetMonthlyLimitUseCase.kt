package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMonthlyLimitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Double> {
        return repository.monthlyLimit
    }
}
