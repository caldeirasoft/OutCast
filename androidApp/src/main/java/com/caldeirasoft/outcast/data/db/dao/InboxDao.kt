package com.caldeirasoft.outcast.data.db.dao

import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface InboxDao : EntityDao<Episode> {

    @Query("""
        INSERT OR REPLACE INTO inbox (feedUrl, guid, added_by_user)
        SELECT e.feedUrl, e.guid, 0
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        WHERE e.feedUrl = :feedUrl
          ORDER BY e.releaseDateTime DESC
        LIMIT 1;
    """)
    suspend fun addMostRecentEpisode(feedUrl: String)

    @Query("""
        INSERT INTO inbox (feedUrl, guid, added_by_user)
        VALUES (:feedUrl, :guid, 1);
    """)
    suspend fun addEpisode(feedUrl: String, guid: String)

    @Query("""
    DELETE FROM inbox
	WHERE EXISTS (
		SELECT e.feedUrl, e.guid
		FROM episode e
		INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
		INNER JOIN podcast_settings ps ON (e.feedUrl = p.feedUrl)
		cross join settings s
		WHERE (e.feedUrl = inbox.feedUrl AND e.guid = inbox.guid AND inbox.added_by_user = 0)
		  AND e.feedUrl = :feedUrl
          AND ps.new_episodes == 1
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
	);
    """)
    suspend fun deleteEpisodesWithUrlAndLimit(feedUrl: String, episodeLimit: Int)

    @Query("DELETE FROM inbox WHERE feedUrl = :feedUrl AND guid = :guid")
    suspend fun deleteEpisodeWithGuid(feedUrl: String, guid: String)
}