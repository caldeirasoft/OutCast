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
        INSERT INTO queue (feedUrl, guid, queueIndex)
        VALUES (:feedUrl, :guid, 0);
    """)
    suspend fun addToQueueNext(feedUrl: String, guid: String)

    @Query("""
        INSERT INTO queue (feedUrl, guid, queueIndex)
        VALUES (:feedUrl, :guid, -1);
    """)
    suspend fun addToQueueLast(feedUrl: String, guid: String)

    @Query("""
        INSERT INTO queue (feedUrl, guid, queueIndex)
        VALUES (:feedUrl, :guid, :queueIndex);
    """)
    suspend fun addToQueue(feedUrl: String, guid: String, queueIndex:Int)

    @Query("""
        DELETE FROM queue
        WHERE feedUrl = :feedUrl AND guid = :guid;
    """)
    suspend fun removeFromQueue(feedUrl: String, guid: String)
}