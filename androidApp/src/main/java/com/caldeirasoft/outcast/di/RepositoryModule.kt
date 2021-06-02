package com.caldeirasoft.outcast.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.db.dao.DownloadDao
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
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
object RepositoryModule {
    @Provides
    fun providePodcastRepository(
        podcastsFetcher: PodcastsFetcher,
        @ApplicationContext context: Context,
        dataStore: DataStore<Preferences>,
        podcastDao: PodcastDao,
        episodeDao: EpisodeDao,
        queueDao: QueueDao,
        json: Json,
    ) =
        PodcastsRepository(podcastsFetcher,
            context,
            dataStore,
            podcastDao,
            episodeDao,
            queueDao,
            json)

    @Provides
    fun provideEpisodesRepository(
        @ApplicationContext context: Context,
        episodeDao: EpisodeDao,
        queueDao: QueueDao
    ) =
        EpisodesRepository(
            context,
            episodeDao,
            queueDao)

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

    @Provides
    fun provideDownloadsRepository(
        @ApplicationContext context: Context,
        downloadDao: DownloadDao
    ) = DownloadRepository(context, downloadDao)
}