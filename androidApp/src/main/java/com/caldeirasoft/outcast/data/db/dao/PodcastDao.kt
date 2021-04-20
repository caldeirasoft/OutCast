package com.caldeirasoft.outcast.data.db.dao

import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.PodcastMetadata
import com.caldeirasoft.outcast.data.db.entities.PodcastMetadata.Companion.metaData
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

@Dao
interface PodcastDao : EntityDao<Podcast> {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    override fun insert(podcast: Podcast): Long

    @Update(entity = Podcast::class)
    fun update(podcastMetadata: PodcastMetadata)

    @Transaction
    fun upsert(podcast: Podcast) {
        val id = insert(podcast)
        if (id == -1L) {
            update(podcast.metaData)
        }
    }

    @Query("SELECT p.* FROM podcast p WHERE isFollowed = 1")
    fun getFollowedPodcasts(): Flow<List<Podcast>>

    @Query("SELECT p.podcastId FROM podcast p WHERE isFollowed = 1 AND podcastId != NULL")
    fun getFollowedPodcastIds(): Flow<List<Long>>

    @Query("SELECT * FROM podcast p WHERE podcastId = :id")
    fun getPodcastWithId(id: Long): Flow<Podcast?>

    @Query("SELECT * FROM podcast p WHERE feedUrl = :feedUrl")
    fun getPodcastWithUrl(feedUrl: String): Flow<Podcast?>

    @Query("UPDATE podcast SET podcastId = :podcastId, artistId = :artistId WHERE feedUrl = :feedUrl")
    suspend fun updatePodcastItunesId(feedUrl: String, podcastId: Long?, artistId: Long?)

    @Query("UPDATE podcast SET releaseDateTime = :releaseDateTime, updatedAt = strftime('%Y-%m-%dT%H:%M:%fZ', 'now') WHERE feedUrl = :feedUrl")
    suspend fun updatePodcastReleaseDate(feedUrl: String, releaseDateTime: Instant)

    @Query("UPDATE podcast SET updatedAt = strftime('%Y-%m-%dT%H:%M:%fZ', 'now') WHERE feedUrl = :feedUrl")
    suspend fun updateLastAccess(feedUrl: String)

    @Query("UPDATE podcast SET isFollowed = 1 WHERE feedUrl = :feedUrl")
    suspend fun followPodcast(feedUrl: String)

    @Query("UPDATE podcast SET isFollowed = 0 WHERE feedUrl = :feedUrl")
    suspend fun unfollowPodcast(feedUrl: String)

    @Query("DELETE FROM podcast WHERE feedUrl = :feedUrl")
    suspend fun deleteWithUrl(feedUrl: String)
}