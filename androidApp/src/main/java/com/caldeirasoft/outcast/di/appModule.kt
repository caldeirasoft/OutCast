package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.createDatabase
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.repository.*
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.presentation.viewmodel.PodcastDetailViewModel
import com.caldeirasoft.outcast.presentation.viewmodel.PodcastsViewModel
import com.caldeirasoft.outcast.presentation.viewmodel.QueueViewModel
import com.caldeirasoft.outcast.ui.screen.store.StoreDataViewModel
import com.caldeirasoft.outcast.ui.screen.store.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.screen.store.StorePodcastViewModel
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


@ExperimentalCoroutinesApi
@FlowPreview
internal val appModule = module {
    single<SqlDriver> { AndroidSqliteDriver(Database.Schema, get(), "outCastDb.db") }
    single { createDatabase(get()) }

    single<PodcastRepository> { PodcastRepositoryImpl(database = get()) }
    single<EpisodeRepository> { EpisodeRepositoryImpl(database = get()) }
    single<InboxRepository> { InboxRepositoryImpl(database = get()) }
    single<QueueRepository> { QueueRepositoryImpl(database = get()) }

    viewModel { InboxViewModel(fetchInboxUseCase = get()) }
    viewModel { QueueViewModel(fetchQueueUseCase = get()) }
    viewModel { PodcastsViewModel(fetchPodcastsSubscribedUseCase = get()) }
    viewModel {
        PodcastDetailViewModel(
            fetchEpisodesFromPodcastUseCase = get(),
            getPodcastUseCase = get()
        )
    }
    viewModel {
        StoreDirectoryViewModel(
            fetchStoreDirectoryUseCase = get(),
            fetchStoreItemsUseCase = get()
        )
    }
    viewModel {
        StoreDataViewModel(
            fetchStoreItemsUseCase = get(),
            fetchStoreDataUseCase = get()
        )
    }
    viewModel {
        StorePodcastViewModel(
            fetchStoreItemsUseCase = get(),
            fetchStorePodcastDataUseCase = get()
        )
    }
}
