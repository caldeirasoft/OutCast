package com.caldeirasoft.outcast.di

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.koin.dsl.module

actual val platformModule = module {
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    factory<HttpClient> {
        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        val httpClient = HttpClient(Android) {
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
}