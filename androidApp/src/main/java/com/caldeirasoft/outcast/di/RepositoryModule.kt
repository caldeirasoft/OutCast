package com.caldeirasoft.outcast.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.InboxDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.dao.QueueDao
import com.caldeirasoft.outcast.data.repository.*
import com.caldeirasoft.outcast.data.util.PodcastsFetcher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    fun providePodcastRepository(
        podcastsFetcher: PodcastsFetcher,
        itunesAPI: ItunesAPI,
        searchAPI: ItunesSearchAPI,
        database: Database,
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>,
        podcastDao: PodcastDao,
        episodeDao: EpisodeDao,
        inboxDao: InboxDao,
        queueDao: QueueDao,
        json: Json,
    ) =
        PodcastsRepository(podcastsFetcher,
            itunesAPI,
            searchAPI,
            database,
            context,
            dataStore,
            podcastDao,
            episodeDao,
            inboxDao,
            queueDao,
            json)

    @Provides
    fun provideLibraryRepository(database: Database) =
        LibraryRepository(database)

    @Provides
    fun provideInboxRepository(database: Database) =
        InboxRepository(database)

    @Provides
    fun provideQueueRepository(database: Database) =
        QueueRepository(database)

    @Provides
    fun provideStoreRepository(
        itunesAPI: ItunesAPI,
        searchAPI: ItunesSearchAPI,
        @ApplicationContext context: Context,
        json: Json,
    ) =
        StoreRepository(itunesAPI, searchAPI, context, json, Dispatchers.Main)

    @Provides
    fun provideDataStoreRepository(
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>,
    ) =
        DataStoreRepository(context, dataStore)
}