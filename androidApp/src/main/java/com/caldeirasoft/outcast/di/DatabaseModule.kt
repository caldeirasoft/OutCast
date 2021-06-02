package com.caldeirasoft.outcast.di

import android.content.Context
import com.caldeirasoft.outcast.data.db.OutcastDb
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ): OutcastDb = OutcastDb.getDatabase(context)
}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseDaoModule {
    @Provides
    fun providePodcastDao(db: OutcastDb) = db.podcastDao()

    @Provides
    fun provideEpisodeDao(db: OutcastDb) = db.episodeDao()

    @Provides
    fun provideQueueDao(db: OutcastDb) = db.queueDao()

    @Provides
    fun provideDownloadDao(db: OutcastDb) = db.downloadDao()
}
