package com.aleon.proyectocellcli.di

import android.os.Build
import androidx.annotation.RequiresApi
import com.aleon.proyectocellcli.domain.repository.ExpenseRepository
import com.aleon.proyectocellcli.repository.FakeExpenseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@RequiresApi(Build.VERSION_CODES.O)
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [RepositoryModule::class] // We replace the real repository module
)
abstract class TestRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(
        fakeExpenseRepository: FakeExpenseRepository
    ): ExpenseRepository
}
