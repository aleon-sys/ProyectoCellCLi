package com.aleon.proyectocellcli.di

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfigImpl @Inject constructor() : AppConfig {
    override val isPro: Boolean = false
}
