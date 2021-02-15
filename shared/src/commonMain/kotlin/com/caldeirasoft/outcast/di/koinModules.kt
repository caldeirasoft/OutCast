package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.interfaces.StorePage
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.usecase.*
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

@ExperimentalSettingsApi
fun KoinApplication.initKoinModules(appModule: Module) {
    modules(commonModule, platformModule, repositoryModule, usecaseModule, appModule)
}

internal val mainDispatcherQualifier = named("MainDispatcher")

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
    single { FetchStoreDirectoryUseCase(storeRepository = get(), localCacheRepository = get()) }
    single { FetchStoreGroupingUseCase(storeRepository = get(), localCacheRepository = get()) }
    single { FetchStoreFrontUseCase(dataStoreRepository = get()) }
    single { FetchStoreDataUseCase(storeRepository = get()) }
    single { FetchStorePodcastDataUseCase(storeRepository = get()) }
    single { FetchStoreEpisodeDataUseCase(storeRepository = get()) }
    single { FetchStoreTopChartsIdsUseCase(storeRepository = get()) }
    single { GetStoreItemsUseCase(storeRepository = get()) }
}

@ExperimentalSettingsApi
internal val repositoryModule = module {
    single { PodcastRepository(database = get()) }
    single { EpisodeRepository(database = get()) }
    single { StoreRepository(httpClient = get()) }
    single { InboxRepository(database = get()) }
    single { QueueRepository(database = get()) }
    single { LocalCacheRepository(settings = get(), json = get()) }
}

internal val commonModule = module {
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
}

internal expect val databaseModule: Module
internal expect val platformModule: Module