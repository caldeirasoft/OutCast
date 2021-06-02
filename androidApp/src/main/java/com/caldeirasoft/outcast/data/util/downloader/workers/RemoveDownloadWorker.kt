package com.caldeirasoft.outcast.data.util.downloader.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.util.downloader.ExoDownloadService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class RemoveDownloadWorker @AssistedInject constructor(
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
        val mediaUrl =
            inputData.getString(DOWNLOAD_ID) ?: return Result.failure()

        val download = downloadRepository.getDownloadWithMediaUrl(mediaUrl)
        if (download != null) {
            downloadRepository.removeDownload(mediaUrl)
            ExoDownloadService.removeDownload(appContext, download.url)
        }
        return Result.success()
    }


    companion object {
        const val DOWNLOAD_ID: String = "download_id"
        private const val DOWNLOAD_WORKER_TAG: String = "downloads"

        /**
         * Start content download
         *
         * @param workManager WorkManager
         * @param mediaUrl
         */
        fun enqueue(
            context: Context,
            mediaUrl: String
        ) {
            val downloadData =
                workDataOf(
                    DOWNLOAD_ID to mediaUrl,
                )

            val workRequest = OneTimeWorkRequestBuilder<RemoveDownloadWorker>()
                .setInputData(downloadData)
                .addTag(DOWNLOAD_WORKER_TAG)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniqueWork(
                    mediaUrl,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
        }
    }
}