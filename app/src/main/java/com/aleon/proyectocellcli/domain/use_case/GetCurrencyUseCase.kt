package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.data.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCurrencyUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<String> {
        return repository.currency
    }
}
