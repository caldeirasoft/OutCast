package com.caldeirasoft.outcast.di

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.createDataStore
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.DataStoreRepositoryAndroid
import com.caldeirasoft.outcast.data.util.network.DnsProviders
import com.caldeirasoft.outcast.data.util.network.RewriteOfflineRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteResponseInterceptor
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.features.cache.*
import io.ktor.client.features.compression.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.dsl.module
import java.io.File

@OptIn(ExperimentalSettingsApi::class)
actual val platformModule = module {
    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }

    single<FlowSettings> {
        val DATA_STORE_FILE_NAME = "user_prefs"
        val dataStore = get<Context>().createDataStore(name = DATA_STORE_FILE_NAME)
        val settings = DataStoreSettings(dataStore)
        settings
    }
    single<DataStoreRepository> { DataStoreRepositoryAndroid(settings = get(), context = get()) }

    single<HttpClient> {
        val cacheSize = 10 * 1024 * 1024 // 10 MiB
        val httpCacheDirectory = File(get<Context>().cacheDir, "httpCache")
        val cache = Cache(httpCacheDirectory, cacheSize.toLong())

        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        val httpClient = HttpClient(OkHttp) {
            install(JsonFeature) {
                serializer = KotlinxSerializer(nonStrictJson)
            }
            ContentEncoding {
                gzip()
                deflate()
            }
            Logging {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("HttpClient", message)
                    }
                }
                level = LogLevel.INFO
            }
            engine {
                val context = get<Context>()
                val customDns = DnsProviders.buildCloudflare(OkHttpClient())

                clientCacheSize = cacheSize
                preconfigured = OkHttpClient.Builder()
                    .cache(cache)
                    .dns(customDns)
                    .build()

                addNetworkInterceptor(RewriteResponseInterceptor())
                addInterceptor(RewriteOfflineRequestInterceptor(context = context))

                // Create the Collector
                val chuckerCollector = ChuckerCollector(
                    context = context,
                    // Toggles visibility of the push notification
                    showNotification = true,
                    // Allows to customize the retention period of collected data
                    retentionPeriod = RetentionManager.Period.ONE_HOUR
                )
                val chuckerInterceptor = ChuckerInterceptor
                    .Builder(context)
                    .collector(chuckerCollector)
                    .build()
                addInterceptor(chuckerInterceptor)
            }
            install(HttpCache)
        }
        httpClient
    }

    /*factory<HttpClient> {
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
    }*/
}