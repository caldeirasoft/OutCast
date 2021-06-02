package com.caldeirasoft.outcast.data.db.dao

import androidx.room.*
import com.caldeirasoft.outcast.data.db.entities.Download
import com.caldeirasoft.outcast.data.db.entities.Episode
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao : EntityDao<Download> {

    @Query("SELECT * FROM downloads WHERE url = :mediaUrl")
    suspend fun getDownloadWithMediaUrl(mediaUrl: String): Download?

    /**
     * Delete queued download
     */
    @Query(
        """DELETE from downloads
      WHERE url in(:downloadUrls)"""
    )
    suspend fun delete(downloadUrls: List<String>)

    /**
     * Update content download progress
     *
     * @param downloadId Download Id
     * @param progress Download progress
     */
    @Query(
        """UPDATE downloads 
             SET progress = :progress,
             state = :state
             WHERE url = :mediaUrl
          """)
    suspend fun updateDownloadProgress(mediaUrl: String, progress: Int, state: Int)

    /**
     * Update content download state
     *
     * @param downloadUrls List of download Ids
     * @param state New download state
     */
    @Query(
        """UPDATE downloads 
             SET state = :state
             WHERE url in(:downloadUrls)
          """
    )
    suspend fun updateDownloadState(downloadUrls: List<String>, state: Int)

    /**
     * Get queued downloads
     */
    @Query(
        """SELECT * FROM downloads
             WHERE state in(:states)
             ORDER BY created_at
             LIMIT :limit
          """)
    @Transaction
    suspend fun getQueuedDownloads(
        limit: Int,
        states: Array<Int>,
    ): List<Download>

    /**
     * Get in progress downloads
     */
    @Query(
        """SELECT * FROM downloads
             WHERE state = :state
             ORDER BY created_at
          """)
    @Transaction
    suspend fun getInProgressDownloads(
        state: Int,
    ): List<Download>

    /**
     * Get in progress downloads
     */
    @Query(
        """SELECT * FROM downloads
             ORDER BY created_at
          """)
    @Transaction
    fun getAllDownloads(): Flow<List<Download>>

}