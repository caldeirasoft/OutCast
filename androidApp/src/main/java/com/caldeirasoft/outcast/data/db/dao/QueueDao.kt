package com.caldeirasoft.outcast.data.db.dao

import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface QueueDao : EntityDao<Episode> {
    @Query("SELECT * FROM episode e INNER JOIN queue USING (feedUrl, guid) ORDER BY queue.queueIndex")
    fun getEpisodes(): Flow<List<Episode>>

    @Query("""
        INSERT INTO queue (feedUrl, guid, queueIndex, added_by_user)
        VALUES (:feedUrl, :guid, 0, 1);
    """)
    suspend fun addToQueueNext(feedUrl: String, guid: String)

    @Query("""
        INSERT INTO queue (feedUrl, guid, queueIndex, added_by_user)
        VALUES (:feedUrl, :guid, -1, 1);
    """)
    suspend fun addToQueueLast(feedUrl: String, guid: String)

    @Query("""
        INSERT INTO queue (feedUrl, guid, queueIndex, added_by_user)
        VALUES (:feedUrl, :guid, :queueIndex, 1);
    """)
    suspend fun addToQueue(feedUrl: String, guid: String, queueIndex:Int)

    @Query("""
        DELETE FROM queue
        WHERE feedUrl = :feedUrl AND guid = :guid;
    """)
    suspend fun removeFromQueue(feedUrl: String, guid: String)

    @Query("""
    DELETE FROM queue
    WHERE EXISTS (
        SELECT e.feedUrl, e.guid
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        INNER JOIN podcast_settings ps ON (e.feedUrl = p.feedUrl)
        cross join settings s
        WHERE e.feedUrl = queue.feedUrl 
          AND e.guid = queue.guid 
          AND queue.queueIndex > 0
          AND queue.added_by_user = 0
          AND e.feedUrl = :feedUrl
          AND CASE :episodeLimit
            WHEN 4 THEN datetime(e.releaseDateTime, 'unixepoch', 'localtime') >  datetime('now', 'start of day')
            WHEN 5 THEN datetime(e.releaseDateTime, 'unixepoch', 'localtime') >  datetime('now', 'start of day', '-6 day')
            WHEN 6 THEN datetime(e.releaseDateTime, 'unixepoch', 'localtime') >  datetime('now', 'start of day', '-13 day')
            WHEN 7 THEN datetime(e.releaseDateTime, 'unixepoch', 'localtime') >  datetime('now', 'start of day', '-1 month')
            ELSE 1=1 END				
        ORDER BY e.releaseDateTime DESC
        LIMIT -1 OFFSET CASE :episodeLimit
            WHEN 0 THEN 1
            WHEN 1 THEN 2
            WHEN 2 THEN 5
            WHEN 3 THEN 10
            ELSE -1 END
    )
    """)
    suspend fun deleteEpisodesWithUrlAndLimit(feedUrl: String, episodeLimit: Int)
}