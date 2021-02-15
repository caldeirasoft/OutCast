package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.DataStoreRepositoryJvm
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.JvmPreferencesSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import io.ktor.client.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.util.prefs.Preferences

@ExperimentalSettingsApi
actual val platformModule = module {
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    factory<HttpClient> {
        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        val httpClient = HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(nonStrictJson)
            }
            ContentEncoding {
                gzip()
                deflate()
            }
        }
        httpClient
    }
    single<FlowSettings> {
        val settings = JvmPreferencesSettings(Preferences.userRoot())
        settings.toFlowSettings()
    }
    single<DataStoreRepository> { DataStoreRepositoryJvm(settings = get()) }
}