package com.caldeirasoft.outcast.di

import android.app.Application
import android.content.Context
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.db.InboxDataSource
import com.caldeirasoft.outcast.data.db.createDatabase
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.QueueRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.network.DnsProviders
import com.caldeirasoft.outcast.data.util.network.GzipRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteOfflineRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteResponseInterceptor
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

fun KoinApplication.initKoinModules() {
    modules(networkModule, databaseModule, repositoryModule, usecaseModule)
}

internal val mainDispatcherQualifier = named("MainDispatcher")

internal val networkModule = module {
    fun provideCache(application: Application): Cache {
        val cacheSize = 10 * 1024 * 1024
        return Cache(application.cacheDir, cacheSize.toLong())
    }

    fun provideHttpClientBuilder(): OkHttpClient.Builder =
        OkHttpClient.Builder().apply {
            connectTimeout(20, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(20, TimeUnit.SECONDS)
            addInterceptor(GzipRequestInterceptor)
            addNetworkInterceptor(StethoInterceptor())
            val customDns = DnsProviders.buildCloudflare(OkHttpClient())
            dns(customDns)
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

    single<CoroutineDispatcher>(mainDispatcherQualifier) { Dispatchers.Main }
    single<Json> {
        val serializer = SerializersModule {
            polymorphic(StoreFeatured::class) {
                subclass(StoreRoom::class)
                subclass(StoreMultiRoom::class)
            }
            polymorphic(StorePage::class) {
                subclass(StoreGroupingPage::class)
                subclass(StoreRoomPage::class)
                subclass(StoreMultiRoomPage::class)
            }
            polymorphic(StoreCollection::class) {
                subclass(StoreCollectionRooms::class)
                subclass(StoreCollectionFeatured::class)
                subclass(StoreCollectionPodcasts::class)
                subclass(StoreCollectionEpisodes::class)
                subclass(StoreCollectionCharts::class)
            }
            polymorphic(StoreItemWithArtwork::class) {
                subclass(StoreRoom::class)
                subclass(StorePodcast::class)
                subclass(StoreEpisode::class)
            }
        }
        Json {
            serializersModule = serializer
            coerceInputValues = false
        }
    }
    single { provideHttpClientBuilder()
        .withChucker(context = androidContext())
        .build()
    }
    single(named("cacheControl")) {
        provideHttpClientBuilder()
            .withChucker(context = androidContext())
            .withCacheControl(context = androidContext())
            .build()
    }
    single<ItunesAPI> { provideRetrofit(client = get(named("cacheControl"))) }
    single<ItunesSearchAPI> { provideRetrofit(client = get(named("cacheControl"))) }
}

internal val databaseModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, get(), "outCastDb.db") }
    single { createDatabase(get()) }
}

internal val repositoryModule = module {
    single<LibraryRepository> { LibraryRepository(database = get()) }
    single<StoreRepository> {
        StoreRepository(itunesAPI = get(),
            searchAPI = get(),
            context = get(),
            json = get(),
            database = get()
        )
    }
    single<InboxDataSource> { InboxDataSource(database = get()) }
    single<QueueRepository> { QueueRepository(database = get()) }
    single<DataStoreRepository> { DataStoreRepository(context = get()) }
}

internal val usecaseModule = module {
    single { LoadFollowedPodcastsUseCase(libraryRepository = get()) }
    single { FetchEpisodesFavoritesUseCase(libraryRepository = get()) }
    single { FetchEpisodesHistoryUseCase(libraryRepository = get()) }
    single { FetchInboxUseCase(inboxRepository = get()) }
    single { FetchQueueUseCase(queueRepository = get()) }
    single {
        SubscribeUseCase(libraryRepository = get(),
            storeRepository = get(),
            dataStoreRepository = get())
    }
    single { UnsubscribeUseCase(podcastRepository = get(), dataStoreRepository = get()) }
    single { LoadPodcastUseCase(podcastRepository = get()) }
    single { LoadStoreGenreDataUseCase(storeRepository = get()) }
    single { LoadStoreDirectoryUseCase(storeRepository = get()) }
    single { LoadStoreDirectoryPagingDataUseCase(storeRepository = get()) }
    single { LoadPodcastEpisodesUseCase(libraryRepository = get()) }
    single {
        LoadPodcastEpisodesPagingDataUseCase(storeRepository = get(),
            libraryRepository = get())
    }
    single { FetchStoreGroupingPagingDataUseCase(storeRepository = get()) }
    single { FetchStoreFrontUseCase(dataStoreRepository = get()) }
    single { FetchStoreRoomPagingDataUseCase(storeRepository = get()) }
    single {
        FetchStorePodcastDataUseCase(storeRepository = get(),
            libraryRepository = get(),
            dataStoreRepository = get())
    }
    single { FetchStoreEpisodeDataUseCase(storeRepository = get(), libraryRepository = get()) }
    single { FetchStoreTopChartsIdsUseCase(storeRepository = get())}
    single { LoadStoreTopChartsPagingDataUseCase(storeRepository = get()) }
    single { GetStoreItemsUseCase(storeRepository = get()) }
    single { LoadSettingsUseCase(dataStoreRepository = get()) }
    single { UpdateSettingsUseCase(dataStoreRepository = get()) }
}

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