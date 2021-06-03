package com.caldeirasoft.outcast.di

import android.content.Context
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caldeirasoft.outcast.data.db.OutcastDatabase
import com.caldeirasoft.outcast.data.db.entities.Settings
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
    fun provideDatabase(
        @ApplicationContext context: Context,
        @RoomCreateCallback callback: RoomDatabase.Callback
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
    fun providePodcastDao(database: OutcastDatabase) = database.podcastDao()

    @Provides
    fun provideEpisodeDao(database: OutcastDatabase) = database.episodeDao()

    @Provides
    fun provideQueueDao(database: OutcastDatabase) = database.queueDao()

    @Provides
    fun provideDownloadDao(database: OutcastDatabase) = database.downloadDao()

    @Provides
    fun provideSettingsDao(database: OutcastDatabase) = database.settingsDao()

    @Provides
    fun providePodcastSettingsDao(database: OutcastDatabase) = database.podcastSettingsDao()

    @Provides
    @Singleton
    @RoomCreateCallback
    fun provideDatabaseCreateCallback(
        outcastDatabase: Provider<OutcastDatabase>,
        @ApplicationScope applicationScope: CoroutineScope
    ) = object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            // insert settings
            val dao = outcastDatabase.get().settingsDao()
            applicationScope.launch {
                dao.insert(Settings())
            }

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
