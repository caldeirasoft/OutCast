package com.caldeirasoft.outcast.data.repository

import android.content.Context
import android.net.Uri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.work.WorkManager
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.dao.DownloadDao
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.dao.QueueDao
import com.caldeirasoft.outcast.data.db.entities.*
import com.caldeirasoft.outcast.data.util.PodcastsFetcher
import com.caldeirasoft.outcast.data.util.downloader.DownloadUtil
import com.caldeirasoft.outcast.data.util.downloader.workers.RemoveDownloadWorker
import com.caldeirasoft.outcast.data.util.downloader.workers.StartDownloadWorker
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class DownloadRepository @Inject constructor(
    val context: Context,
    val downloadDao: DownloadDao
) {
    /**
     * Update download progress
     */
    suspend fun updateDownloadProgress(mediaUrl: String, progress: Int, state: DownloadState) {
        downloadDao.updateDownloadProgress(
            mediaUrl,
            progress,
            state.ordinal
        )
    }

    /**
     * Update download progress
     */
    suspend fun updateDownloadState(downloadUris: List<String>, state: DownloadState) {
        downloadDao.updateDownloadState(downloadUris, state.ordinal)
    }

    /**
     * Add new download
     */
    suspend fun addDownload(mediaUrl: String) {
        val download = Download.with(mediaUrl)
        downloadDao.insert(download)
    }

    /**
     * Remove a download
     */
    suspend fun removeDownload(mediaUrl: String) {
        downloadDao.delete(listOf(mediaUrl))
    }

    /**
     * Get queued downloads
     *
     * @param limit no. of pending downloads to fetch
     * @param states download states
     */
    suspend fun getQueuedDownloads(
        limit: Int = 1,
        states: Array<DownloadState>,
    ): List<Download> = downloadDao.getQueuedDownloads(
        limit,
        states.map { it.ordinal }.toTypedArray()
    )

    /**
     * Get in progress downloads
     */
    suspend fun getInProgressDownloads(): List<Download> =
        downloadDao.getInProgressDownloads(DownloadState.IN_PROGRESS.ordinal)

    /**
     * Get downloads
     */
    fun getAllDownloads(): Flow<List<Download>> = downloadDao.getAllDownloads()

    /**
     * Get queued download by id
     *
     * @param downloadId Download id
     */
    suspend fun getDownloadWithMediaUrl(mediaUrl: String): Download? =
        downloadDao
            .getDownloadWithMediaUrl(mediaUrl)

    /**
     * Start download
     */
    fun startDownload(episode: Episode) {
        StartDownloadWorker.enqueue(
            context = context,
            mediaUrl = episode.mediaUrl,
            downloadOnlyOnWifi = true
        )
    }

    /**
     * Start download
     */
    fun removeDownload(episode: Episode) {
        RemoveDownloadWorker.enqueue(
            context = context,
            mediaUrl = episode.mediaUrl,
        )
    }
}