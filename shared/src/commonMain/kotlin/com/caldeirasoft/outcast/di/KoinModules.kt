package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.data.repository.StoreRepositoryImpl
import com.caldeirasoft.outcast.domain.repository.StoreRepository
import com.caldeirasoft.outcast.domain.usecase.*
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun KoinApplication.initKoinModules(appModule: Module) {
    modules(commomModule, platformModule, appModule)
}

internal val mainDispatcherQualifier = named("MainDispatcher")

internal val commomModule = module {
    single<StoreRepository> { StoreRepositoryImpl(httpClient = get()) }

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
    factory { GetPodcastUseCase(podcastRepository = get()) }
    factory { GetEpisodeUseCase(episodeRepository = get()) }
    factory { FetchStoreDirectoryUseCase(storeRepository = get()) }
    factory { FetchStorePodcastDataUseCase(storeRepository = get()) }
    factory { FetchStoreItemsUseCase(storeRepository = get()) }
    factory { FetchStoreDataUseCase(storeRepository = get()) }
}

internal expect val platformModule: Module