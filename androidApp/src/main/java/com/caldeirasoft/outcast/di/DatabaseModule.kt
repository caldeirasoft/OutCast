package com.caldeirasoft.outcast.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.caldeirasoft.outcast.data.db.OutcastDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): OutcastDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            OutcastDatabase::class.java,
            "outcast.db"
        )
        .addCallback(OutcastDatabase.callback)
        .fallbackToDestructiveMigration()
        .build()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() =
        CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun providePodcastDao(database: OutcastDatabase) = database.podcastDao()

    @Provides
    @Singleton
    fun provideEpisodeDao(database: OutcastDatabase) = database.episodeDao()

    @Provides
    @Singleton
    fun provideQueueDao(database: OutcastDatabase) = database.queueDao()

    @Provides
    @Singleton
    fun provideInboxDao(database: OutcastDatabase) = database.inboxDao()

    @Provides
    @Singleton
    fun provideDownloadDao(database: OutcastDatabase) = database.downloadDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: OutcastDatabase) = database.settingsDao()

    @Provides
    @Singleton
    fun provideSearchDao(database: OutcastDatabase) = database.searchDao()

    @Provides
    @Singleton
    fun providePodcastSettingsDao(database: OutcastDatabase) = database.podcastSettingsDao()
}
