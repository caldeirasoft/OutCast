package com.caldeirasoft.outcast.di

import android.content.ContentValues
import android.content.Context
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caldeirasoft.outcast.data.db.OutcastDatabase
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.domain.enums.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        callback: RoomDatabase.Callback
    ): OutcastDatabase = Room
        .databaseBuilder(
            context.applicationContext,
            OutcastDatabase::class.java,
            "outcast.db")
        .addCallback(callback)
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
    fun provideDownloadDao(database: OutcastDatabase) = database.downloadDao()

    @Provides
    @Singleton
    fun provideSettingsDao(database: OutcastDatabase) = database.settingsDao()

    @Provides
    @Singleton
    fun providePodcastSettingsDao(database: OutcastDatabase) = database.podcastSettingsDao()

    @Provides
    @Singleton
    fun provideDatabaseCreateCallback() = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // insert settings
            val settingsValues = ContentValues()
            settingsValues.put("id", 1)
            settingsValues.put("sync_podcasts", true)
            settingsValues.put("background_sync", BackgroundRefreshOptions.EVERY_1_HOUR.ordinal)
            settingsValues.put("sync_with_cloud", true)
            settingsValues.put("episode_limit", EpisodeLimitOptions.ONE_MONTH.ordinal)
            settingsValues.put("download_queue_episodes", true)
            settingsValues.put("download_saved_episodes", true)
            settingsValues.put("delete_played_episodes", DeleteEpisodesDelay.AFTER_1_DAY.ordinal)
            settingsValues.put("stream_on_mobile_data", StreamOptions.PLAY.ordinal)
            settingsValues.put("sync_on_mobile_data", true)
            settingsValues.put("download_on_mobile_data", true)
            settingsValues.put("skip_back_button", SkipOptions.SKIP_15_SECONDS.ordinal)
            settingsValues.put("skip_forward_button", SkipOptions.SKIP_30_SECONDS.ordinal)
            settingsValues.put("external_controls", ExternalControlsOptions.SKIP_FORWARD_BACK.ordinal)
            settingsValues.put("theme", Theme.AUTO.ordinal)
            db.insert("settings", OnConflictStrategy.ABORT, settingsValues)

            // insert into queue move queueIndex
            db.execSQL(
                """
                        CREATE TRIGGER insert_queue_update_index
                        BEFORE INSERT ON queue
                        BEGIN
                            UPDATE queue SET queueIndex = queueIndex + 1
                            WHERE queueIndex >= new.queueIndex;
                        END;
                    """.trimIndent()
            )

            // insert into queue last (queueIndex = -1) => update queueIndex
            db.execSQL(
                """
                        CREATE TRIGGER insert_queue_last
                        AFTER INSERT ON queue
                        FOR EACH ROW WHEN (new.queueIndex == -1)
                        BEGIN
                            REPLACE INTO queue (feedUrl, guid, queueIndex)
                            SELECT new.feedUrl, new.guid, COUNT(*)
                            FROM queue;
                        END;
                    """.trimIndent()
            )

            // update queueIndex after deletion
            db.execSQL(
                """
                        CREATE TRIGGER delete_queue_update_index
                        AFTER DELETE ON queue
                        BEGIN
                            UPDATE queue SET queueIndex = queueIndex - 1
                            WHERE queueIndex > old.queueIndex;
                        END;
                    """.trimIndent()
            )
        }
    }
}
