package com.aleon.proyectocellcli.di

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeAppConfig @Inject constructor() : AppConfig {
    override var isPro: Boolean = true
}
