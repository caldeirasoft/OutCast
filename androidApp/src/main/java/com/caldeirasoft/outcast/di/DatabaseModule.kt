package com.caldeirasoft.outcast.di

import android.content.Context
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.createDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object DatabaseModuleModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): Database =
        createDatabase(AndroidSqliteDriver(Database.Schema, appContext, "outCastDb.db"))

    @Provides
    fun provideHttpClient(): HttpClient {
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
        return httpClient
    }
}
