package com.caldeirasoft.outcast.data.util.downloader.workers

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.caldeirasoft.outcast.data.db.entities.DownloadState
import com.caldeirasoft.outcast.data.repository.DownloadRepository
import com.caldeirasoft.outcast.data.util.downloader.ExoDownloadService
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.math.roundToInt

@HiltWorker
class UpdateDownloadProgressWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
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
    override suspend fun doWork(): Result = coroutineScope {
        Timber.d("In progress download : %s", downloadManager.currentDownloads.size)
        while (downloadManager.currentDownloads.isNotEmpty()) {
            downloadManager.currentDownloads.forEach { download ->
                val request = download.request
                val percent = download.percentDownloaded.roundToInt()
                downloadRepository.updateDownloadProgress(request.id, percent, DownloadState.IN_PROGRESS)
                Timber.d("Update download progress %s %s %s", request.id, percent, DownloadState.IN_PROGRESS)
            }
            withContext(Dispatchers.IO) {
                delay(100)
            }
        }
        Result.success()
    }

    companion object {
        private const val DOWNLOAD_WORKER_TAG: String = "download_progress"

        /**
         * Start content download
         *
         * @param workManager WorkManager
         * @param downloadId
         * @param progress
         * @param state
         */
        fun execute(context: Context) {
            val updateDownloadProgressWorkRequest =
                OneTimeWorkRequestBuilder<UpdateDownloadProgressWorker>()
                    .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                    DOWNLOAD_WORKER_TAG,
                    ExistingWorkPolicy.KEEP,
                    updateDownloadProgressWorkRequest)
        }
    }
}