package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.data.db.createDatabase
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.repository.*
import org.koin.dsl.module


internal val appModule = module {
    single { createDatabase(get()) }

    single<PodcastRepository> { PodcastRepositoryImpl(database = get()) }
    single<EpisodeRepository> { EpisodeRepositoryImpl(database = get()) }
    single<InboxRepository> { InboxRepositoryImpl(database = get()) }
    single<QueueRepository> { QueueRepositoryImpl(database = get()) }
}
