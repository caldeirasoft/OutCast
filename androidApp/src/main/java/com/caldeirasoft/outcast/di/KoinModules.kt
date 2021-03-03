package com.caldeirasoft.outcast.di

import android.app.Application
import android.content.Context
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.db.createDatabase
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.data.util.network.DnsProviders
import com.caldeirasoft.outcast.data.util.network.GzipRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteOfflineRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteResponseInterceptor
import com.caldeirasoft.outcast.di.adapters.InstantStringJsonAdapter
import com.caldeirasoft.outcast.di.adapters.LocalDateJsonAdapter
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
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

fun KoinApplication.initKoinModules(appModule: Module) {
    modules(networkModule, databaseModule, repositoryModule, usecaseModule, appModule)
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
                subclass(StorePodcastPage::class)
                subclass(StoreRoomPage::class)
                subclass(StoreMultiRoomPage::class)
            }
            polymorphic(StoreCollection::class) {
                subclass(StoreCollectionRooms::class)
                subclass(StoreCollectionFeatured::class)
                subclass(StoreCollectionItems::class)
                subclass(StoreCollectionTopPodcasts::class)
                subclass(StoreCollectionTopEpisodes::class)
            }
            polymorphic(StoreItemWithArtwork::class) {
                subclass(StoreRoomFeatured::class)
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
    single { providesMoshi() }
    single<ItunesAPI> { provideRetrofit(client = get(named("cacheControl")), moshi = get()) }
    single<ItunesSearchAPI> { provideRetrofit(client = get(named("cacheControl")), moshi = get()) }
}

internal val databaseModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, get(), "outCastDb.db") }
    single { createDatabase(get()) }
}

internal val repositoryModule = module {
    single { PodcastRepository(database = get()) }
    single { EpisodeRepository(database = get()) }
    single { StoreRepository(itunesAPI = get(), searchAPI = get(), context = get(), json = get()) }
    single { InboxRepository(database = get()) }
    single { QueueRepository(database = get()) }
    single { LocalCacheRepository(context = get(), json = get()) }
    single { DataStoreRepository(context = get()) }
}

internal val usecaseModule = module {
    single { FetchPodcastsSubscribedUseCase(podcastRepository = get()) }
    single { FetchEpisodesFromPodcastUseCase(episodeRepository = get()) }
    single { FetchEpisodesFavoritesUseCase(episodeRepository = get()) }
    single { FetchEpisodesHistoryUseCase(episodeRepository = get()) }
    single { FetchFavoriteEpisodesCountUseCase(episodeRepository = get()) }
    single { FetchPlayedEpisodesCountUseCase(episodeRepository = get()) }
    single { FetchCountEpisodesBySectionUseCase(episodeRepository = get()) }
    single { FetchInboxUseCase(inboxRepository = get()) }
    single { FetchQueueUseCase(queueRepository = get()) }
    single { SubscribeToPodcastUseCase(podcastRepository = get()) }
    single { UnsubscribeFromPodcastUseCase(podcastRepository = get()) }
    single { FetchPodcastUseCase(podcastRepository = get()) }
    single { FetchEpisodeUseCase(episodeRepository = get()) }
    single { LoadStoreGenreDataUseCase(storeRepository = get()) }
    single { LoadStoreDirectoryUseCase(storeRepository = get())}
    single { LoadStoreDirectoryPagingDataUseCase(storeRepository = get()) }
    single { FetchStoreGroupingPagingDataUseCase(storeRepository = get(), localCacheRepository = get())}
    single { FetchStoreFrontUseCase(dataStoreRepository = get()) }
    single { FetchStoreDataUseCase(storeRepository = get()) }
    single { FetchStoreRoomPagingDataUseCase(storeRepository = get()) }
    single { FetchStorePodcastDataUseCase(storeRepository = get()) }
    single { FetchStoreEpisodeDataUseCase(storeRepository = get()) }
    single { FetchStoreTopChartsIdsUseCase(storeRepository = get())}
    single { GetStoreItemsUseCase(storeRepository = get()) }
}

fun providesMoshi(): Moshi = Moshi.Builder()
    .add(Instant::class.java, InstantStringJsonAdapter())
    .add(LocalDate::class.java, LocalDateJsonAdapter())
    .add(KotlinJsonAdapterFactory()) // To read field names using reflection
    .build()

@OptIn(ExperimentalSerializationApi::class)
inline fun <reified T> provideRetrofit(client: OkHttpClient, moshi: Moshi): T {
    val baseURL = T::class.java.getField("baseUrl").get(null) as String
    val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .client(client)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    return retrofit.create(T::class.java)
}