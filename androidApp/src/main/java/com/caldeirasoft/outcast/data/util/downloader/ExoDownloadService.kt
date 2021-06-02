package com.caldeirasoft.outcast.data.util.downloader

import android.app.Notification
import android.content.Context
import android.net.Uri
import androidx.work.WorkManager
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.DownloadState
import com.caldeirasoft.outcast.data.util.downloader.workers.UpdateDownloadProgressWorker
import com.caldeirasoft.outcast.data.util.downloader.workers.UpdateDownloadWorker
import com.google.android.exoplayer2.offline.Download
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadRequest
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.offline.DownloadService.DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Scheduler
import com.google.android.exoplayer2.ui.DownloadNotificationHelper
import com.google.android.exoplayer2.util.NotificationUtil
import com.google.android.exoplayer2.util.Util
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.nio.charset.Charset
import javax.inject.Inject
import kotlin.math.roundToInt

@AndroidEntryPoint
class ExoDownloadService : DownloadService(
    FOREGROUND_NOTIFICATION_ID,
    DEFAULT_FOREGROUND_NOTIFICATION_UPDATE_INTERVAL,
    CHANNEL_ID,
    R.string.exo_download_notification_channel_name,
    R.string.exo_download_notification_channel_description
) , DownloadManager.Listener {
    private var nextNotificationId = FOREGROUND_NOTIFICATION_ID + 1
    lateinit var downloadNotificationHelper: DownloadNotificationHelper

    @Inject
    lateinit var _downloadManager: DownloadManager

    override fun onCreate() {
        super.onCreate()
        downloadNotificationHelper = DownloadNotificationHelper(this, CHANNEL_ID)
    }


    /**
     * See [DownloadService.getDownloadManager]
     */
    override fun getDownloadManager(): DownloadManager {
        // This will only happen once, because getDownloadManager is guaranteed to be called only once
        // in the life cycle of the process.
        val downloadManager = _downloadManager
        downloadManager.addListener(this)
        return downloadManager
    }

    /**
     * See [DownloadService.getScheduler]
     */
    override fun getScheduler(): Scheduler = PlatformScheduler(this, JOB_ID)

    /**
     * See [DownloadManager.Listener.onDownloadChanged]
     */
    override fun onDownloadChanged(
        downloadManager: DownloadManager,
        download: Download,
        finalException: Exception?
    ) {
        Timber.d("OnDownloadChanged: %s %s", download.request.id, download.state)
        val notification: Notification = when (download.state) {
            Download.STATE_COMPLETED -> {
                handledDownloadCompleted(download)
                getCompletedNotification(download)
            }
            Download.STATE_FAILED -> {
                handledDownloadFailed(download)
                getFailedNotification(download)
            }
            Download.STATE_REMOVING -> {
                handledDownloadRemoved(download)
                return
            }
            Download.STATE_DOWNLOADING -> {
                handledDownloadProgress()
                return
            }
            else -> { return }
        }

        NotificationUtil.setNotification(this, nextNotificationId++, notification)
    }

    private fun handledDownloadProgress() {
        UpdateDownloadProgressWorker.execute(
            context = this
        )
    }


    /**
     * See [DownloadService.getForegroundNotification]
     */
    override fun getForegroundNotification(downloads: MutableList<Download>): Notification {
        val message = downloads
            .firstOrNull { it.state == Download.STATE_DOWNLOADING }
            ?.request?.data?.toString(Charset.defaultCharset())
        return downloadNotificationHelper.buildProgressNotification(
            this,
            R.drawable.ic_analytics,
            null,
            message,
            downloads
        )
    }

    private fun handledDownloadFailed(download: Download) {
        UpdateDownloadWorker.updateAndStartNext(
            workManager = WorkManager.getInstance(this),
            downloadId = download.request.id,
            progress = download.percentDownloaded.roundToInt(),
            state = DownloadState.FAILED,
            downloadOnlyOnWifi = true
        )
    }

    private fun handledDownloadRemoved(download: Download) {
        UpdateDownloadWorker.updateAndStartNext(
            workManager = WorkManager.getInstance(this),
            downloadId = download.request.id,
            progress = 0,
            state = DownloadState.NONE,
            downloadOnlyOnWifi = true
        )
    }

    private fun getFailedNotification(download: Download): Notification =
        downloadNotificationHelper.buildDownloadFailedNotification(
            applicationContext,
            android.R.drawable.ic_menu_save,
            null,
            Util.fromUtf8Bytes(download.request.data)
        )

    private fun handledDownloadCompleted(download: Download) {
        UpdateDownloadWorker.updateAndStartNext(
            workManager = WorkManager.getInstance(this),
            downloadId = download.request.id,
            progress = download.percentDownloaded.roundToInt(),
            state = DownloadState.COMPLETED,
            downloadOnlyOnWifi = true
        )
    }

    private fun getCompletedNotification(download: Download): Notification =
        downloadNotificationHelper.buildDownloadCompletedNotification(
            applicationContext,
            android.R.drawable.ic_menu_save,
            null,
            Util.fromUtf8Bytes(download.request.data)
        )

    companion object {
        private const val CHANNEL_ID = "notification.channel.downloads"
        private const val JOB_ID = 1
        private const val FOREGROUND_NOTIFICATION_ID = 1

        /**
         * Start a download
         *
         * @param ctx Context
         * @param contentId Content Id
         * @param uri Download uri
         *
         * @return Download id (Content id)
         */
        fun startDownload(
            ctx: Context,
            contentId: String,
            uri: Uri,
            name: String?
        ): String {
            val downloadRequest = DownloadRequest.Builder(uri.toString(), uri).build()
            sendAddDownload(ctx, ExoDownloadService::class.java, downloadRequest, true)
            return downloadRequest.id
        }

        /**
         * Stop a download
         *
         * @param ctx Context
         * @param contentId Content Id
         */
        fun removeDownload(
            ctx: Context,
            contentId: String
        ) {
            sendRemoveDownload(ctx, ExoDownloadService::class.java, contentId, true)
        }

        /**
         * Remove all downloads
         *
         * @param ctx Context
         */
        fun removeAllDownloads(ctx: Context) {
            sendRemoveAllDownloads(ctx, ExoDownloadService::class.java, true)
        }
    }
}