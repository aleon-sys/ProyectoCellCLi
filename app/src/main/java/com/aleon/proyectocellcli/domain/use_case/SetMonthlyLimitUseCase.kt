package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.data.repository.UserPreferencesRepository
import javax.inject.Inject

class SetMonthlyLimitUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(limit: Double) {
        repository.setMonthlyLimit(limit)
    }
}
