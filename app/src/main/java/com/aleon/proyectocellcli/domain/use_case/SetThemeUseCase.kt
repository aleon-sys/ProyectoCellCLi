package com.aleon.proyectocellcli.domain.use_case

import com.aleon.proyectocellcli.data.repository.UserPreferencesRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(theme: String) {
        repository.setTheme(theme)
    }
}
