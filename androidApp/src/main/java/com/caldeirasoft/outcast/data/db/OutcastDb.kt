package com.caldeirasoft.outcast.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.dao.QueueDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.Queue
import com.caldeirasoft.outcast.data.db.typeconverters.InstantConverter

@androidx.room.Database(
    entities = [Podcast::class, Episode::class, Queue::class],
    version = 2
)
@TypeConverters(InstantConverter::class)
abstract class OutcastDb : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun queueDao(): QueueDao

    companion object {
        private var INSTANCE: OutcastDb? = null

        fun getDatabase(context: Context): OutcastDb =
            INSTANCE ?: Room
                .databaseBuilder(
                    context.applicationContext,
                    OutcastDb::class.java,
                    "outcast.db")
                .addCallback(DB_CALLBACK)
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }

        private val DB_CALLBACK = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
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
}