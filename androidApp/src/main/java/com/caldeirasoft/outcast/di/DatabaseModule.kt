package com.caldeirasoft.outcast.di

import android.content.Context
import android.os.Debug
import androidx.room.Room
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.db.OutcastDb
import com.caldeirasoft.outcast.data.db.createDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    fun provideDatabaseSqlDelight(@ApplicationContext appContext: Context): Database =
        createDatabase(AndroidSqliteDriver(Database.Schema, appContext, "outCastDb.db"))

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
    fun provideInboxDao(db: OutcastDb) = db.inboxDao()

    @Provides
    fun provideQueueDao(db: OutcastDb) = db.queueDao()
}
