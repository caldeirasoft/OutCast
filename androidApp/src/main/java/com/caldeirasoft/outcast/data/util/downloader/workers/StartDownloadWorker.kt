package com.caldeirasoft.outcast.data.util.downloader.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.caldeirasoft.outcast.data.db.entities.Download.Companion.isCompleted
import com.caldeirasoft.outcast.data.db.entities.Download.Companion.isInProgress
import com.caldeirasoft.outcast.data.db.entities.Download.Companion.isPaused
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class StartDownloadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
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
        val mediaUrl =
            inputData.getString(DOWNLOAD_ID) ?: return Result.failure()

        val download = downloadRepository.getDownloadWithMediaUrl(mediaUrl)
        return when {
            download.isInProgress || download.isPaused -> {
                val outputData = workDataOf(
                    DownloadWorker.DOWNLOAD_ID to download?.url
                )
                Result.success(outputData)
            }
            download.isCompleted -> Result.success()
            else -> {
                // queue downloads for next worker
                if (mediaUrl.isNotEmpty()) {
                    downloadRepository.addDownload(mediaUrl)
                }
                Result.success()
            }
        }
    }

    companion object {
        const val DOWNLOAD_ID: String = "download_id"
        private const val DOWNLOAD_WORKER_TAG: String = "downloads"

        /**
         * Start content download
         *
         * @param workManager WorkManager
         * @param episodeId
         * @param progress
         * @param state
         */
        fun enqueue(
            workManager: WorkManager,
            mediaUrl: String,
            downloadOnlyOnWifi: Boolean
        ) {
            val downloadData =
                workDataOf(
                    DOWNLOAD_ID to mediaUrl,
                )

            val startDownloadWorkRequest = OneTimeWorkRequestBuilder<StartDownloadWorker>()
                .setInputData(downloadData)
                .addTag(DOWNLOAD_WORKER_TAG)
                .build()

            val downloadWorkRequest =
                DownloadWorker.buildWorkRequest(downloadOnlyOnWifi)

            workManager
                .beginUniqueWork(
                    DOWNLOAD_ID,
                    ExistingWorkPolicy.REPLACE,
                    startDownloadWorkRequest
                )
                .then(downloadWorkRequest)
                .enqueue()
        }
    }
}