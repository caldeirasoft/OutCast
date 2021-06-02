package com.caldeirasoft.outcast.data.util.downloader.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.caldeirasoft.outcast.data.db.entities.DownloadState
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class PendingDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameter: WorkerParameters,
    /**
     * Download manager
     * */
    val downloadManager: DownloadManager,
    /**
     * Download repository
     * */
    val downloadRepository: DownloadRepository
) : CoroutineWorker(appContext, workerParameter) {
    /**
     * See [Worker.doWork]
     */
    override suspend fun doWork(): Result {
        val inProgressDownloadsUrlsFromDb = downloadRepository
            .getInProgressDownloads()
            .map { it.url }
        val inProgressDownloads = downloadManager.currentDownloads
        if (!inProgressDownloads.isNullOrEmpty()) {
            val downloadIds = inProgressDownloadsUrlsFromDb.subtract(
                inProgressDownloads.map { it.request.id }
            ).toList()

            if (downloadIds.isNotEmpty()) {
                downloadRepository.updateDownloadState(downloadIds, state = DownloadState.CREATED)
            }
        }
        return Result.success()
    }

    companion object {
        private const val DOWNLOAD_WORKER_NAME: String = "pending_download"

        /**
         * Queue verify download worker
         */
        fun enqueue(workManager: WorkManager, downloadOnlyOnWifi: Boolean) {
            val updatePendingDownload = OneTimeWorkRequestBuilder<PendingDownloadWorker>()
                .build()

            val downloadWorkRequest = DownloadWorker.buildWorkRequest(downloadOnlyOnWifi)

            workManager
                .beginUniqueWork(
                    DOWNLOAD_WORKER_NAME,
                    ExistingWorkPolicy.REPLACE,
                    updatePendingDownload
                )
                .then(downloadWorkRequest)
                .enqueue()
        }
    }
}