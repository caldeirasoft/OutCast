package com.caldeirasoft.outcast.di

import android.content.Context
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.createDatabase
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.domain.repository.*
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Binds
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
abstract class CoreModule {
    @Binds
    abstract fun bindsPodcastRepository(podcastRepositoryImpl: PodcastRepositoryImpl): PodcastRepository

    @Binds
    abstract fun bindsEpisodeRepository(episodeRepositoryImpl: EpisodeRepositoryImpl): EpisodeRepository

    @Binds
    abstract fun bindsInboxRepository(inboxRepositoryImpl: InboxRepositoryImpl): InboxRepository

    @Binds
    abstract fun bindsQueueRepository(queueRepositoryImpl: QueueRepositoryImpl): QueueRepository

    @Binds
    abstract fun bindsStoreRepository(storeRepositoryImpl: StoreRepositoryImpl): StoreRepository
}