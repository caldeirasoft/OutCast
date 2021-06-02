package com.caldeirasoft.outcast.data.util.downloader.workers

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.caldeirasoft.outcast.data.db.entities.DownloadState
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.util.downloader.ExoDownloadService
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted workerParameter: WorkerParameters,
    /**
     * Download repository
     * */
    val downloadRepository: DownloadRepository
) : CoroutineWorker(appContext, workerParameter) {
    /**
     * See [Worker.doWork]
     */
    override suspend fun doWork(): Result {
        val downloadId = inputData.getString(DOWNLOAD_ID)
        if (downloadId == null) {
            handleQueuedDownloads()
        }

        return Result.success()
    }

    private suspend fun handleQueuedDownloads(): Result {
        // Get all queued downloads
        val queuedDownloads =
            downloadRepository.getQueuedDownloads(
                limit = MAX_PARALLEL_DOWNLOADS,
                states = arrayOf(DownloadState.CREATED),
            )

        // Get download urls for queued downloads
        queuedDownloads.map { download ->
            ExoDownloadService.startDownload(
                appContext,
                download.url,
                Uri.parse(download.url),
                null
            )
        }

        return Result.success()
    }

    companion object {
        const val DOWNLOAD_ID: String = "download_id"
        private const val MAX_PARALLEL_DOWNLOADS: Int = 1
        private const val DOWNLOAD_WORKER_TAG: String = "downloads"

        /**
         * Build Download work request
         */
        fun buildWorkRequest(downloadOnlyOnWifi: Boolean = false): OneTimeWorkRequest {
            val networkType = if (downloadOnlyOnWifi) {
                NetworkType.UNMETERED
            } else {
                NetworkType.CONNECTED
            }
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(networkType)
                .setRequiresStorageNotLow(true)
                .build()

            return OneTimeWorkRequestBuilder<DownloadWorker>()
                .setConstraints(constraints)
                .addTag(DOWNLOAD_WORKER_TAG)
                .build()
        }
    }
}