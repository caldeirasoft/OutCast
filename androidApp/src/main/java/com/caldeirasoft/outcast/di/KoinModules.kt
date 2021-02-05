package com.caldeirasoft.outcast.di

import android.content.Context
import android.util.Log
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.createDatabase
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.data.util.network.RewriteOfflineRequestInterceptor
import com.caldeirasoft.outcast.data.util.network.RewriteResponseInterceptor
import com.caldeirasoft.outcast.domain.interfaces.*
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.repository.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
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
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.io.File

fun KoinApplication.initKoinModules(appModule: Module) {
    modules(platformModule, commomModule, appModule)
}

internal val mainDispatcherQualifier = named("MainDispatcher")

internal val platformModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, get(), "outCastDb.db") }
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
        }
    }
    single<NetworkFlipperPlugin> { NetworkFlipperPlugin() }
    factory<HttpClient> {
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
                clientCacheSize = cacheSize
                preconfigured = OkHttpClient.Builder()
                    .cache(cache)
                    .build()

                addNetworkInterceptor(FlipperOkhttpInterceptor(get()))
                addNetworkInterceptor(RewriteResponseInterceptor())
                addInterceptor(RewriteOfflineRequestInterceptor(get()))
            }
            install(HttpCache)
        }
        httpClient
    }
}

internal val commomModule = module {
    single { createDatabase(get()) }

    single<PodcastRepository> { PodcastRepositoryImpl(database = get()) }
    single<EpisodeRepository> { EpisodeRepositoryImpl(database = get()) }
    single<StoreRepository> { StoreRepositoryImpl(httpClient = get()) }
    single<InboxRepository> { InboxRepositoryImpl(database = get()) }
    single<QueueRepository> { QueueRepositoryImpl(database = get()) }
    single<LocalCacheRepository> { LocalCacheRepositoryImpl(context = get(), json = get()) }

    factory { FetchPodcastsSubscribedUseCase(podcastRepository = get()) }
    factory { FetchEpisodesFromPodcastUseCase(episodeRepository = get()) }
    factory { FetchEpisodesFavoritesUseCase(episodeRepository = get()) }
    factory { FetchEpisodesHistoryUseCase(episodeRepository = get()) }
    factory { FetchFavoriteEpisodesCountUseCase(episodeRepository = get()) }
    factory { FetchPlayedEpisodesCountUseCase(episodeRepository = get()) }
    factory { FetchCountEpisodesBySectionUseCase(episodeRepository = get()) }
    factory { FetchInboxUseCase(inboxRepository = get()) }
    factory { FetchQueueUseCase(queueRepository = get()) }
    factory { SubscribeToPodcastUseCase(podcastRepository = get()) }
    factory { UnsubscribeFromPodcastUseCase(podcastRepository = get()) }
    factory { FetchPodcastUseCase(podcastRepository = get()) }
    factory { FetchEpisodeUseCase(episodeRepository = get()) }
    factory { FetchStoreDirectoryUseCase(storeRepository = get(), localCacheRepository = get()) }
    factory { FetchStoreGroupingUseCase(storeRepository = get(), localCacheRepository = get())}
    factory { FetchStoreFrontUseCase(dataStoreRepository = get()) }
    factory { FetchStoreDataUseCase(storeRepository = get()) }
    factory { FetchStorePodcastDataUseCase(storeRepository = get()) }
    factory { FetchStoreGenresUseCase(storeRepository = get())}
    factory { FetchStoreTopChartsIdsUseCase(storeRepository = get())}
    factory { GetStoreItemsUseCase(storeRepository = get()) }

}