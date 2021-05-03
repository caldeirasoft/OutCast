package com.caldeirasoft.outcast.data.db.dao

import androidx.paging.DataSource
import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.EpisodeMetadata
import com.caldeirasoft.outcast.data.db.entities.EpisodeMetadata.Companion.metadata
import com.caldeirasoft.outcast.data.db.entities.EpisodeWithPodcast
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

    @Query("SELECT * FROM episode e WHERE feedUrl = :feedUrl ORDER BY e.releaseDateTime DESC")
    fun getEpisodesDataSourceWithUrl(feedUrl: String): DataSource.Factory<Int, Episode>

    @Query("SELECT * FROM episode e WHERE isFavorite = 1")
    fun getFavoriteEpisodes(): Flow<List<Episode>>

    @Query("SELECT * FROM episode e WHERE playbackPosition != NULL OR isPlayed = 1")
    fun getEpisodesHistory(): Flow<List<Episode>>

    @Query("""
        UPDATE episode 
        SET playbackPosition = :playbackPosition AND updatedAt = strftime('%Y-%m-%dT%H:%M:%fZ', 'now') 
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun addEpisodeToHistory(feedUrl: String, guid: String, playbackPosition: Long)

    @Query("""
        UPDATE episode 
        SET isFavorite = 1
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun addEpisodeToFavorites(feedUrl: String, guid: String)

    @Query("""
        UPDATE episode 
        SET isFavorite = 0 
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun removeEpisodeToFavorites(feedUrl: String, guid: String)

    @Query("""
        UPDATE episode 
        SET playbackPosition = NULL, isPlayed = 1, playedAt = strftime('%Y-%m-%dT%H:%M:%fZ', 'now')
        WHERE feedUrl = :feedUrl AND guid = :guid
    """)
    suspend fun markEpisodeAsPlayed(feedUrl: String, guid: String)

    @Query("DELETE FROM episode WHERE feedUrl = :feedUrl AND guid = :guid")
    suspend fun deleteEpisodeWithGuid(feedUrl: String, guid: String)
}