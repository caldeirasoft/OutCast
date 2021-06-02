package com.caldeirasoft.outcast.di

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSource
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.Cache
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.Executors
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DownloadModule {
    @Provides
    @Singleton
    fun provideSimpleCache(
        application: Application,
        databaseProvider: DatabaseProvider
    ): Cache =
        SimpleCache(getDownloadDirectory(application), NoOpCacheEvictor(), databaseProvider)

    // Use the library provided implementation of DatabaseProvider.
    // Data save in exoplayer_internal.db.
    @Provides
    @Singleton
    fun provideDatabaseProvider(application: Application): DatabaseProvider = ExoDatabaseProvider(application)

    @Provides
    @Singleton
    fun provideHttpDataSourceFactory(@ApplicationContext context: Context): HttpDataSource.Factory =
        OkHttpDataSource.Factory(OkHttpClient())

    @Provides
    @Singleton
    fun provideDownloadManager(
        application: Application,
        databaseProvider: DatabaseProvider,
        downloadCache: Cache,
        httpDataSourceFactory: HttpDataSource.Factory
    ): DownloadManager =
        DownloadManager(
            application,
            databaseProvider,
            downloadCache,
            httpDataSourceFactory,
            Executors.newFixedThreadPool(6)
        ).apply {
            maxParallelDownloads = 2
        }


    private fun getDownloadDirectory(context: Context): File {
        val downloadDirectory = context.getExternalFilesDir(null) ?: context.filesDir
        return File(downloadDirectory, DOWNLOAD_CONTENT_DIRECTORY)
    }

    private const val DOWNLOAD_NOTIFICATION_CHANNEL = "download_channel"
    private const val TAG = "DownloadUtil"
    private const val DOWNLOAD_CONTENT_DIRECTORY = "downloads"
}