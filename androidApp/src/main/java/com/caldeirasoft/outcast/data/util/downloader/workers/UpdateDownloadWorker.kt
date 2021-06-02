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
class UpdateDownloadWorker @AssistedInject constructor(
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
        val downloadId =
            inputData.getString(DOWNLOAD_ID) ?: return Result.failure()

        val downloadProgress = inputData.getInt(DOWNLOAD_PROGRESS, 0)

        val downloadState = inputData.getInt(DOWNLOAD_STATE, DownloadState.FAILED.ordinal)

        downloadRepository.updateDownloadProgress(
            downloadId,
            downloadProgress,
            DownloadState.values()[downloadState]
        )

        return Result.success()
    }

    companion object {
        const val DOWNLOAD_ID: String = "download_id"
        const val DOWNLOAD_PROGRESS: String = "download_progress"
        const val DOWNLOAD_STATE: String = "download_state"

        /**
         * Start content download
         *
         * @param workManager WorkManager
         * @param downloadId
         * @param progress
         * @param state
         */
        fun updateAndStartNext(
            workManager: WorkManager,
            downloadId: String,
            progress: Int,
            state: DownloadState,
            downloadOnlyOnWifi: Boolean
        ) {
            val downloadData =
                workDataOf(
                    DOWNLOAD_ID to downloadId,
                    DOWNLOAD_PROGRESS to progress,
                    DOWNLOAD_STATE to state.ordinal
                )

            val startDownloadWorkRequest = OneTimeWorkRequestBuilder<UpdateDownloadWorker>()
                .setInputData(downloadData)
                .build()

            val downloadWorkRequest =
                DownloadWorker.buildWorkRequest(downloadOnlyOnWifi)

            workManager
                .beginUniqueWork(
                    downloadId,
                    ExistingWorkPolicy.REPLACE,
                    startDownloadWorkRequest
                )
                .then(downloadWorkRequest)
                .enqueue()
        }
    }
}