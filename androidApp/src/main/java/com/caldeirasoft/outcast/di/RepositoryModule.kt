package com.caldeirasoft.outcast.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.db.dao.*
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
        inboxDao: InboxDao,
        queueDao: QueueDao,
        settingsDao: SettingsDao,
        podcastSettingsDao: PodcastSettingsDao,
        json: Json,
    ) =
        PodcastsRepository(podcastsFetcher,
            context,
            dataStore,
            podcastDao,
            episodeDao,
            inboxDao,
            queueDao,
            settingsDao,
            podcastSettingsDao,
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
        @ApplicationContext context: Context,
        json: Json,
    ) =
        StoreRepository(itunesAPI, context, json, Dispatchers.Main)

    @Provides
    fun provideSearchRepository(
        podcastDao: PodcastDao,
        episodeDao: EpisodeDao,
        searchDao: SearchDao,
        json: Json,
    ) =
        SearchRepository(podcastDao, episodeDao, searchDao, json)

    @Provides
    fun provideDownloadsRepository(
        @ApplicationContext context: Context,
        downloadDao: DownloadDao
    ) = DownloadRepository(context, downloadDao)

    @Provides
    fun provideSettingsRepository(
        @ApplicationContext context: Context,
        settingsDao: SettingsDao,
        podcastSettingsDao: PodcastSettingsDao
    ) = SettingsRepository(context, settingsDao, podcastSettingsDao)
}