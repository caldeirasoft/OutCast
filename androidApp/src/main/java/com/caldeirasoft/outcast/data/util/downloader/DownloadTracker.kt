package com.caldeirasoft.outcast.data.util.downloader

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.os.StatFs
import android.util.Log
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.offline.*
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.CopyOnWriteArraySet
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.math.absoluteValue

class DownloadTracker @Inject constructor(
    private val context: Context,
    private val httpDataSourceFactory: HttpDataSource.Factory,
    private val downloadManager: DownloadManager
) {
    interface Listener {
        fun onDownloadsChanged(download: Download)
    }

    private val FILE_NAME_SANITIZE_PATTERN = Pattern.compile("[^a-zA-Z0-9-_.]")
    private val DIRECTORY_EPISODES = "Episodes" //TODO: companion
    private val listeners: CopyOnWriteArraySet<Listener> = CopyOnWriteArraySet()
    private val downloadIndex: DownloadIndex = downloadManager.downloadIndex
    private val downloads: HashMap<Uri, Download> = hashMapOf()
    private var availableBytesLeft: Long = StatFs(DownloadUtil.getDownloadDirectory(context).path).availableBytes

    init {
        downloadManager.addListener(DownloadManagerListener())
        loadDownloads()
    }

    /**
     * Load downloads
     */
    private fun loadDownloads() {
        try {
            downloadIndex.getDownloads().use { loadedDownloads ->
                while (loadedDownloads.moveToNext()) {
                    val download = loadedDownloads.download
                    downloads[download.request.uri] = download
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "Failed to query download", e)
        }
    }

    fun addListener(listener: Listener) {
        Assertions.checkNotNull(listener)
        listeners.add(listener)
    }

    fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    fun isDownloaded(mediaItem: MediaItem): Boolean {
        val download = downloads[mediaItem.playbackProperties?.uri]
        return download != null && download.state == Download.STATE_COMPLETED
    }

    fun hasDownload(uri: Uri?): Boolean = downloads.keys.contains(uri)

    fun getDownloadRequest(uri: Uri?): DownloadRequest? {
        uri ?: return null
        val download = downloads[uri]
        return if (download != null && download.state != Download.STATE_FAILED) download.request else null
    }

    fun addDownload(
        uri: Uri,
    ) {
        val downloadRequest = DownloadRequest.Builder(uri.toString(), uri).build()
        DownloadService.sendAddDownload(
            context,
            ExoDownloadService::class.java,
            downloadRequest,
            false)
    }

    fun stopDownload(
        context: Context,
        mediaItem: MediaItem,
    ) {
        val download = downloads[mediaItem.playbackProperties?.uri]
        if (download != null) {
            if (download.state == Download.STATE_STOPPED) {
                DownloadService.sendSetStopReason(
                    context,
                    ExoDownloadService::class.java,
                    download.request.id,
                    Download.STOP_REASON_NONE,
                    true
                )
            } else {
                DownloadService.sendSetStopReason(
                    context,
                    ExoDownloadService::class.java,
                    download.request.id,
                    Download.STATE_STOPPED,
                    false
                )
            }
        }
    }

    fun removeDownload(uri: Uri?) {
        val download = downloads[uri]
        download?.let {
            DownloadService.sendRemoveDownload(
                context,
                ExoDownloadService::class.java,
                download.request.id,
                false,
            )
        }
    }

    @ExperimentalCoroutinesApi
    suspend fun getCurrentProgressDownload(uri: Uri?): Flow<Float?> {
        var percent: Float? = downloadManager.currentDownloads.find { it.request.uri == uri }?.percentDownloaded
        return callbackFlow {
            while (percent != null) {
                percent = downloadManager.currentDownloads.find{ it.request.uri == uri }?.percentDownloaded
                offer(percent)
                withContext(Dispatchers.IO) {
                    delay(1000)
                }
            }
        }
    }

    private fun getDownloadHelper(mediaItem: MediaItem): DownloadHelper {
        return when(mediaItem.playbackProperties?.mimeType) {
            MimeTypes.APPLICATION_MPD, MimeTypes.APPLICATION_M3U8, MimeTypes.APPLICATION_SS -> {
                DownloadHelper.forMediaItem(
                    context,
                    mediaItem,
                    DefaultRenderersFactory(context),
                    httpDataSourceFactory
                )
            }
            else -> DownloadHelper.forMediaItem(context, mediaItem)
        }
    }

    //https://github.com/SoftwareEngineeringDaily/software-engineering-daily-android/
    //https://github.com/smashinggit/eyepetizer/blob/776e9d6c6263874eaafbed761ad7beb4544453a1/common/src/main/java/com/cs/common/download/AndroidDownloadManager.kt
    //https://github.com/xleo1989/AppHelper/blob/10f5b7cdd21c178c26a3175634212e0734d69750/src/main/java/com/x/leo/apphelper/download/UserDownloadManager.kt
    //https://github.com/Jacknic/Learn-Android/blob/e53efbadcccf69b65f5ded0914d19c6c70e3de18/download/src/main/java/com/jacknic/android/download/MainActivity.kt

    //https://stackoverflow.com/questions/7824835/android-downloadmanager-progress

    private fun getEpisodeFile(episode: Episode): File =
        File(getMediaDirectory(), urlToFileName(episode.mediaUrl))


    private fun getMediaDirectory(): File =
        File(context.getExternalFilesDir(null), "episodes").apply {
            mkdirs()
        }//+ ctx.getString(R.string.directory_episodes);

    private fun urlToFileName(url: String): String {
        var filename = url.substring(url.lastIndexOf('/') + 1)
        filename = filename.takeLast(90)
        val hash = url.hashCode().absoluteValue

        if (filename.contains("?")) {
            filename = filename.substring(0, filename.indexOf("?"))
        }
        val extension: String
        if (filename.contains(".")) {
            extension = filename.substring(filename.lastIndexOf("."))
            filename = filename.substring(0, filename.lastIndexOf("."))
        } else {
            extension = ".$filename"
            filename = "no-name"
        }
        filename = "${filename}-${hash}${extension}"
        filename = FILE_NAME_SANITIZE_PATTERN.matcher(filename).replaceAll("_")

        return filename
    }

    private inner class DownloadManagerListener : DownloadManager.Listener {
        override fun onDownloadChanged(
            downloadManager: DownloadManager,
            download: Download,
            finalException: Exception?
        ) {
            downloads[download.request.uri] = download
            for (listener in listeners) {
                listener.onDownloadsChanged(download)
            }

            if (download.state == Download.STATE_COMPLETED) {
                availableBytesLeft += Util.fromUtf8Bytes(download.request.data).toLong() - download.bytesDownloaded
            }
        }

        override fun onDownloadRemoved(downloadManager: DownloadManager, download: Download) {
            downloads.remove(download.request.uri)
            for (listener in listeners) {
                listener.onDownloadsChanged(download)
            }

            availableBytesLeft += if (download.percentDownloaded == 100f) {
                download.bytesDownloaded
            } else {
                Util.fromUtf8Bytes(download.request.data).toLong()
            }
        }
    }
}