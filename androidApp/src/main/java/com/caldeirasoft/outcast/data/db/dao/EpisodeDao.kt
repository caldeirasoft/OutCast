package com.caldeirasoft.outcast.data.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.*
import com.caldeirasoft.outcast.data.db.entities.EpisodeMetadata.Companion.metadata
import kotlinx.coroutines.flow.Flow

@Dao
interface EpisodeDao : EntityDao<Episode> {
    @Update(entity = Episode::class)
    fun update(episodeMetadata: EpisodeMetadata)

    @Transaction
    fun upsertAll(objList: List<Episode>) = insertAll(objList)
        .withIndex()
        .filter { it.value == -1L }
        .forEach { update(objList[it.index].metadata) }

    @Transaction
    @Query("SELECT * FROM episode e WHERE feedUrl = :feedUrl AND guid = :guid")
    fun getEpisodeWithGuid(feedUrl: String, guid: String): Flow<EpisodeWithPodcast?>

    @Query("SELECT * FROM episode e WHERE feedUrl = :feedUrl")
    fun getEpisodesWithUrl(feedUrl: String): Flow<List<Episode>>

    @Query("""
        SELECT e.* 
        FROM episode e
         LEFT JOIN podcast p USING (feedUrl)
        WHERE feedUrl = :feedUrl 
        ORDER BY 
            CASE WHEN p.podcast_sort = 0 THEN e.releaseDateTime END ASC,
            CASE WHEN p.podcast_sort = 1 THEN e.releaseDateTime END DESC
    """)
    fun getEpisodesDataSourceWithUrl(feedUrl: String): DataSource.Factory<Int, Episode>

    @Query("SELECT * FROM episode e WHERE feedUrl = :feedUrl ORDER BY e.releaseDateTime ASC")
    fun getEpisodesDataSourceWithUrlOrderByDateAsc(feedUrl: String): DataSource.Factory<Int, Episode>

    // get saved episodes podcast count
    @Query("SELECT * FROM episode e WHERE isSaved = 1")
    fun getSavedEpisodesDataSource(): DataSource.Factory<Int, Episode>

    // get saved episodes podcast count
    @Query("""
        SELECT DISTINCT p.feedUrl, p.artworkUrl, p.name, COUNT(e.guid) AS count
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        WHERE isSaved = 1
        GROUP BY p.feedUrl, p.artworkUrl, p.name
    """)
    fun getSavedEpisodesPodcastCount(): Flow<List<PodcastWithCount>>

    // get history episodes
    @Query("SELECT * FROM episode e WHERE playback_position != NULL")
    fun getEpisodesHistoryDataSource(): DataSource.Factory<Int, Episode>

    // get history episodes podcast count
    @Query("""
        SELECT DISTINCT p.feedUrl, p.artworkUrl, p.name, COUNT(e.guid) AS count
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        WHERE playback_position != NULL
        GROUP BY p.feedUrl, p.artworkUrl, p.name
    """)
    fun getEpisodesHistoryPodcastCount(): Flow<List<PodcastWithCount>>

    // get inbox episodes
    @Query("""
        SELECT e.* FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        INNER JOIN inbox i ON (e.feedUrl = i.feedUrl AND e.guid = i.guid)
        ORDER BY e.releaseDateTime DESC
    """)
    fun getInboxEpisodesDataSource(): DataSource.Factory<Int, Episode>

    // get inbox podcast count
    @Query("""
        SELECT DISTINCT p.feedUrl, p.artworkUrl, p.name, COUNT(e.guid) AS count
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        INNER JOIN inbox i ON (e.feedUrl = i.feedUrl AND e.guid = i.guid)
        GROUP BY p.feedUrl, p.artworkUrl, p.name
    """)
    fun getInboxEpisodesPodcastCount(): Flow<List<PodcastWithCount>>

    @Query("""
        SELECT e.*
        FROM episode e
        INNER JOIN episode_fts USING (name, podcastName)
        WHERE episode_fts MATCH :query
    """)
    fun searchEpisodes(query: String): DataSource.Factory<Int, Episode>

    @Query("""
        UPDATE episode 
        SET playback_position = :playbackPosition AND updatedAt = strftime('%Y-%m-%dT%H:%M:%fZ', 'now') 
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun addEpisodeToHistory(feedUrl: String, guid: String, playbackPosition: Long)

    @Query("""
        UPDATE episode 
        SET isSaved = 1, saved_at = strftime('%Y-%m-%dT%H:%M:%fZ', 'now')
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun saveEpisodeToLibrary(feedUrl: String, guid: String)

    @Query("""
        UPDATE episode 
        SET isSaved = 0, saved_at = NULL 
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun deleteFromLibrary(feedUrl: String, guid: String)

    @Query("""
        UPDATE episode 
        SET playback_position = duration, playback_played_at = strftime('%Y-%m-%dT%H:%M:%fZ', 'now')
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun markEpisodeAsPlayed(feedUrl: String, guid: String)

    @Query("DELETE FROM episode WHERE feedUrl = :feedUrl AND guid = :guid")
    suspend fun deleteEpisodeWithGuid(feedUrl: String, guid: String)
}