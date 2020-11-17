package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.domain.repository.StoreRepository
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun KoinApplication.initKoinModules(appModule: Module) {
    modules(commomModule, platformModule, appModule)
}

internal val mainDispatcherQualifier = named("MainDispatcher")

internal val commomModule = module {
}

internal expect val platformModule: Module