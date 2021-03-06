package com.caldeirasoft.outcast.data.db

import android.content.ContentValues
import android.content.Context
import androidx.room.OnConflictStrategy
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caldeirasoft.outcast.data.db.dao.*
import com.caldeirasoft.outcast.data.db.entities.*
import com.caldeirasoft.outcast.data.db.typeconverters.InstantConverter
import com.caldeirasoft.outcast.domain.enums.*
import kotlinx.datetime.Clock

@androidx.room.Database(
    entities = [
        Podcast::class,
        PodcastFTS::class,
        Episode::class,
        EpisodeFTS::class,
        Queue::class,
        Inbox::class,
        Download::class,
        Settings::class,
        PodcastSettings::class,
        SearchEntity::class,
    ],
    version = 6
)
@TypeConverters(InstantConverter::class)
abstract class OutcastDatabase : RoomDatabase() {
    abstract fun podcastDao(): PodcastDao
    abstract fun episodeDao(): EpisodeDao
    abstract fun queueDao(): QueueDao
    abstract fun inboxDao(): InboxDao
    abstract fun downloadDao(): DownloadDao
    abstract fun settingsDao(): SettingsDao
    abstract fun searchDao(): SearchDao
    abstract fun podcastSettingsDao(): PodcastSettingsDao

    companion object {
        fun getCallback(context: Context) = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val nowMillis = Clock.System.now().toEpochMilliseconds()

                // insert settings
                val settingsValues = ContentValues()
                settingsValues.put("id", 1)
                settingsValues.put("sync_podcasts", true)
                settingsValues.put("background_sync", BackgroundRefreshOptions.EVERY_1_HOUR.ordinal)
                settingsValues.put("sync_with_cloud", true)
                settingsValues.put("sync_on_mobile_data", true)
                settingsValues.put("episode_limit", EpisodeLimitOptions.ONE_MONTH.ordinal)
                settingsValues.put("allow_notifications", true)
                settingsValues.put("notifications_type",
                    NotificationsTypeOptions.NEW_EPISODES_INBOX.id +
                    NotificationsTypeOptions.NEW_EPISODES_QUEUE.id
                )
                settingsValues.put("notifications_badge",
                    NotificationsBadgeOptions.INBOX_COUNT.id
                )
                settingsValues.put("download_queue_episodes", true)
                settingsValues.put("download_queue_length", 80)
                settingsValues.put("download_starred_episodes", true)
                settingsValues.put(
                    "delete_played_episodes",
                    DeleteEpisodesDelayOptions.AFTER_1_DAY.ordinal
                )
                settingsValues.put("stream_on_mobile_data", StreamOptions.PLAY.ordinal)
                settingsValues.put("download_on_mobile_data", false)
                settingsValues.put("skip_back_button", SkipOptions.SKIP_15_SECONDS.ordinal)
                settingsValues.put("skip_forward_button", SkipOptions.SKIP_30_SECONDS.ordinal)
                settingsValues.put(
                    "external_controls",
                    ExternalControlsOptions.SKIP_FORWARD_BACK.ordinal
                )
                settingsValues.put(
                    "store_country",
                    context.resources.configuration.locales.get(0).country)
                settingsValues.put("last_sync_at", nowMillis)
                settingsValues.put("last_refresh_at", nowMillis)
                settingsValues.put("theme", Theme.AUTO.ordinal)
                db.insert("settings", OnConflictStrategy.ABORT, settingsValues)

                // insert into queue => update queueIndex for following items
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

                // insert into queue => remove from inbox
                db.execSQL(
                    """
CREATE TRIGGER add_to_queue_remove_from_inbox
AFTER INSERT ON queue
BEGIN
    DELETE FROM inbox
     WHERE feedUrl = new.feedUrl AND guid = new.guid;
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
    REPLACE INTO queue (feedUrl, guid, added_by_user, queueIndex)
    SELECT new.feedUrl, new.guid, new.added_by_user, COUNT(*)
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

                // on unfollowed podcast => remove from inbox + remove settings
                db.execSQL(
                    """
CREATE TRIGGER unfollow_podcast_delete_inbox
AFTER UPDATE OF isFollowed ON podcast
FOR EACH ROW WHEN new.isFollowed = 0
BEGIN
    DELETE FROM inbox
      WHERE feedUrl = new.feedUrl;
    DELETE FROM podcast_settings
      WHERE feedUrl = new.feedUrl;
END;
                    """.trimIndent()
                )

                // on new episodes => add to inbox
                db.execSQL(
                    """
CREATE TRIGGER add_episode_into_inbox
AFTER INSERT ON episode
BEGIN
    INSERT OR REPLACE INTO inbox (feedUrl, guid, added_by_user)
    SELECT e.feedUrl, e.guid, 0
    FROM episode e
    INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
    INNER JOIN podcast_settings ps ON (e.feedUrl = p.feedUrl)
    WHERE e.feedUrl = new.feedUrl AND e.guid = new.guid
            AND p.isFollowed == 1 AND ps.new_episodes == 0
            AND e.releaseDateTime > p.releaseDateTime;
END;
            """.trimIndent()
                )

                // on new episodes => add to queue next
                db.execSQL(
                    """
CREATE TRIGGER add_episode_into_queue_next
AFTER INSERT ON episode
BEGIN
	INSERT OR REPLACE INTO queue (feedUrl, guid, added_by_user, queueIndex)
	SELECT e.feedUrl, e.guid, 0, 0
	FROM episode e
	INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
	INNER JOIN podcast_settings ps ON (e.feedUrl = p.feedUrl)
	WHERE e.feedUrl = new.feedUrl AND e.guid = new.guid
			AND p.isFollowed == 1 AND ps.new_episodes == 1
			AND e.releaseDateTime > p.releaseDateTime;
END;
            """.trimIndent()
                )

                // on new episodes => add to queue last
                db.execSQL(
                    """
CREATE TRIGGER add_episode_into_queue_last
AFTER INSERT ON episode
BEGIN
	INSERT OR REPLACE INTO queue (feedUrl, guid, added_by_user, queueIndex)
	SELECT e.feedUrl, e.guid, 0, -1
	FROM episode e
	INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
	INNER JOIN podcast_settings ps ON (e.feedUrl = p.feedUrl)
	WHERE e.feedUrl = new.feedUrl AND e.guid = new.guid
			AND p.isFollowed == 1 AND ps.new_episodes == 2
			AND e.releaseDateTime > p.releaseDateTime;
END;
            """.trimIndent()
                )

                // podcast fts
                db.execSQL(
                    """
                        INSERT INTO podcast_fts(podcast_fts) VALUES ('rebuild');
                    """.trimIndent()
                )

                // episode fts
                db.execSQL(
                    """
                        INSERT INTO episode_fts(episode_fts) VALUES ('rebuild');
                    """.trimIndent()
                )
            }
        }
    }
}