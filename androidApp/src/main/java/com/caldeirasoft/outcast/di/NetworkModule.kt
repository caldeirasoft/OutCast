package com.caldeirasoft.outcast.di

import android.app.Application
import android.content.Context
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.util.PodcastsFetcher
import com.caldeirasoft.outcast.data.util.network.DnsProviders
import com.caldeirasoft.outcast.data.util.network.GzipRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteOfflineRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteResponseInterceptor
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.store.*
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {
    @Provides
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    @Provides
    fun provideHttpClientBuilder(): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            connectTimeout(20, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(20, TimeUnit.SECONDS)
            addInterceptor(GzipRequestInterceptor)
            addNetworkInterceptor(StethoInterceptor())
            //val customDns = DnsProviders.buildCloudflareIp(OkHttpClient())
            //dns(customDns)
        }

    fun OkHttpClient.Builder.withCacheControl(context: Context) = this.apply {
        addNetworkInterceptor(RewriteResponseInterceptor())
        addInterceptor(RewriteOfflineRequestInterceptor(context))
    }

    fun OkHttpClient.Builder.withChucker(context: Context) = this.apply {
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

    //single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    @Provides
    fun provideJson(): Json {
        val serializer = SerializersModule {
            polymorphic(StoreCollection::class) {
                subclass(StoreCollectionData::class)
                subclass(StoreCollectionFeatured::class)
                subclass(StoreCollectionItems::class)
            }
            polymorphic(StoreItemArtwork::class) {
                subclass(StoreData::class)
                subclass(StorePodcast::class)
                subclass(StoreEpisode::class)
            }
        }
        return Json {
            serializersModule = serializer
            coerceInputValues = false
            ignoreUnknownKeys = true
        }
    }

    @Provides
    fun provideOkHttpClient(@ApplicationContext appContext: Context): OkHttpClient {
        return provideHttpClientBuilder()
            .withChucker(context = appContext)
            .build()
    }

    //@Provides
    fun provideOkHttpClientWithCacheControl(@ApplicationContext appContext: Context): OkHttpClient {
        return provideHttpClientBuilder()
            .withChucker(context = appContext)
            .withCacheControl(context = appContext)
            .build()
    }

    @Provides
    fun provideItunesAPI(@ApplicationContext appContext: Context): ItunesAPI =
        provideRetrofit(provideOkHttpClient(appContext))

    @Provides
    fun provideItunesSearchAPI(@ApplicationContext appContext: Context): ItunesSearchAPI =
        provideRetrofit(provideOkHttpClient(appContext))

    @Provides
    fun providePodcastsFetcher(@ApplicationContext appContext: Context) =
        PodcastsFetcher(
            okHttpClient = provideOkHttpClient(appContext),
            ioDispatcher = Dispatchers.IO
        )

    @Provides
    @OptIn(ExperimentalSerializationApi::class)
    inline fun <reified T> provideRetrofit(client: OkHttpClient): T {
        val baseURL = T::class.java.getField("baseUrl").get(null) as String
        val contentType = "application/json".toMediaType()
        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(client)
            .addConverterFactory(nonStrictJson.asConverterFactory(contentType = contentType))
            .build()

        return retrofit.create(T::class.java)
    }
}

