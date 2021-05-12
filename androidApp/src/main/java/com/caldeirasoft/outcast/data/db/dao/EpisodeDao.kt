package com.caldeirasoft.outcast.data.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.EpisodeMetadata
import com.caldeirasoft.outcast.data.db.entities.EpisodeMetadata.Companion.metadata
import com.caldeirasoft.outcast.data.db.entities.EpisodeWithPodcast
import com.caldeirasoft.outcast.domain.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.days

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

    @Query("SELECT * FROM episode e WHERE feedUrl = :feedUrl ORDER BY e.releaseDateTime DESC")
    fun getEpisodesDataSourceWithUrl(feedUrl: String): DataSource.Factory<Int, Episode>

    @Query("SELECT * FROM episode e WHERE isSaved = 1")
    fun getSavedEpisodes(): Flow<List<Episode>>

    @Query("SELECT * FROM episode e WHERE playbackPosition != NULL OR isPlayed = 1")
    fun getEpisodesHistory(): Flow<List<Episode>>

    // get latest episodes (unplayed) / last 3 months
    @Query("""
        SELECT e.* FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        LEFT JOIN queue q ON (e.feedUrl = q.feedUrl AND e.guid = q.guid)
        WHERE p.isFollowed = 1 AND e.isPlayed = 0 AND e.playbackPosition IS NULL AND q.feedUrl IS NULL
          AND e.releaseDateTime > :releaseDateTime
        ORDER BY e.releaseDateTime DESC
    """)
    fun getLatestEpisodesDataSource(releaseDateTime: Instant = Clock.System.now().minus(90.days)): DataSource.Factory<Int, Episode>

    // get latest episodes (unplayed) categories / last 3 months
    @Query("""
        SELECT DISTINCT e.category
        FROM episode e
        INNER JOIN podcast p ON (e.feedUrl = p.feedUrl)
        LEFT JOIN queue q ON (e.feedUrl = q.feedUrl AND e.guid = q.guid)
        WHERE p.isFollowed = 1 AND e.isPlayed = 0 AND e.playbackPosition IS NULL AND q.feedUrl IS NULL
          AND e.releaseDateTime > :releaseDateTime
    """)
    fun getLatestEpisodesCategories(releaseDateTime: Instant = Clock.System.now().minus(90.days)): Flow<List<Category?>>

    @Query("""
        UPDATE episode 
        SET playbackPosition = :playbackPosition AND updatedAt = strftime('%Y-%m-%dT%H:%M:%fZ', 'now') 
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun addEpisodeToHistory(feedUrl: String, guid: String, playbackPosition: Long)

    @Query("""
        UPDATE episode 
        SET isSaved = 1
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun saveEpisodeToLibrary(feedUrl: String, guid: String)

    @Query("""
        UPDATE episode 
        SET isSaved = 0 
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun deleteFromLibrary(feedUrl: String, guid: String)

    @Query("""
        UPDATE episode 
        SET playbackPosition = NULL, isPlayed = 1, playedAt = strftime('%Y-%m-%dT%H:%M:%fZ', 'now')
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun markEpisodeAsPlayed(feedUrl: String, guid: String)

    @Query("DELETE FROM episode WHERE feedUrl = :feedUrl AND guid = :guid")
    suspend fun deleteEpisodeWithGuid(feedUrl: String, guid: String)
}