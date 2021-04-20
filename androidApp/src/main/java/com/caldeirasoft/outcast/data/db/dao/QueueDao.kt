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
        INSERT OR REPLACE INTO queue (feedUrl, guid, queueIndex)
        SELECT e.feedUrl, e.guid, 0
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        WHERE e.feedUrl = :feedUrl
         AND e.releaseDateTime > :releaseDateTime;
    """)
    suspend fun addRecentEpisodesIntoQueueFirst(feedUrl: String, releaseDateTime: Instant)

    @Query("""
        INSERT OR REPLACE INTO queue (feedUrl, guid, queueIndex)
        SELECT e.feedUrl, e.guid, -1
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        WHERE e.feedUrl = :feedUrl
         AND e.releaseDateTime > :releaseDateTime;
    """)
    suspend fun addRecentEpisodesIntoQueueLast(feedUrl: String, releaseDateTime: Instant)

    @Query("DELETE FROM inbox WHERE feedUrl = :feedUrl AND guid = :guid")
    suspend fun deleteEpisodeWithGuid(feedUrl: String, guid: String)
}