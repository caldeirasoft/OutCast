package com.caldeirasoft.outcast.data.db.dao

import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface InboxDao : EntityDao<Episode> {


    @Query("""
        INSERT OR REPLACE INTO inbox (feedUrl, guid)
        SELECT e.feedUrl, e.guid
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        WHERE e.feedUrl = :feedUrl
          ORDER BY e.releaseDateTime DESC
        LIMIT 1;
    """)
    suspend fun addMostRecentEpisode(feedUrl: String)

    @Query("""
        INSERT OR REPLACE INTO inbox (feedUrl, guid)
        SELECT e.feedUrl, e.guid
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        WHERE e.feedUrl = :feedUrl
         AND e.releaseDateTime > :releaseDateTime;
    """)
    suspend fun addRecentEpisodes(feedUrl: String, releaseDateTime: Instant)

    @Query("""
        DELETE FROM inbox
        WHERE EXISTS (
            SELECT feedUrl, guid
            FROM episode
            WHERE (episode.feedUrl = inbox.feedUrl AND episode.guid = inbox.guid)
            AND feedUrl = :feedUrl
            ORDER BY releaseDateTime DESC
            LIMIT -1 OFFSET :offset
        )
    """)
    suspend fun deleteEpisodesWithUrlAndLimit(feedUrl: String, offset: Int)

    @Query("DELETE FROM inbox WHERE feedUrl = :feedUrl AND guid = :guid")
    suspend fun deleteEpisodeWithGuid(feedUrl: String, guid: String)
}