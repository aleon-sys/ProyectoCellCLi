package com.aleon.proyectocellcli.di

import com.aleon.proyectocellcli.di.AppConfig
import javax.inject.Inject
import javax.inject.Singleton

// A fake, mutable AppConfig for use in tests to simulate free/pro versions.
@Singleton
class FakeAppConfig @Inject constructor() : AppConfig {
    override var isPro: Boolean = true // Default to pro for most tests
}
