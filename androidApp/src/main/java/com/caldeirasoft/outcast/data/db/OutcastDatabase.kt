package com.caldeirasoft.outcast.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caldeirasoft.outcast.data.db.dao.*
import com.caldeirasoft.outcast.data.db.entities.*
import com.caldeirasoft.outcast.data.db.typeconverters.InstantConverter

@androidx.room.Database(
    entities = [
        Podcast::class,
        Episode::class,
        Queue::class,
        Download::class,
        Settings::class,
        PodcastSettings::class,
    ],
    version = 1
)
@TypeConverters(InstantConverter::class)
abstract class OutcastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun queueDao(): QueueDao
    abstract fun downloadDao(): DownloadDao
    abstract fun settingsDao(): SettingsDao
    abstract fun podcastSettingsDao(): PodcastSettingsDao
}