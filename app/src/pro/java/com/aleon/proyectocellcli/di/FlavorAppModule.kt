package com.aleon.proyectocellcli.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorAppModule {

    @Binds
    @Singleton
    abstract fun bindAppConfig(impl: AppConfigImpl): AppConfig
}
