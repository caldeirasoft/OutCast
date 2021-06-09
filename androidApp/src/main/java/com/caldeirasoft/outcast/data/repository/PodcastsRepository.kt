package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.dao.PodcastSettingsDao
import com.caldeirasoft.outcast.data.db.dao.QueueDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.PodcastItunesMetadata
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.util.PodcastsFetcher
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class PodcastsRepository @Inject constructor(
    private val podcastsFetcher: PodcastsFetcher,
    val context: Context,
    val dataStore: DataStore<Preferences>,
    val podcastDao: PodcastDao,
    val episodeDao: EpisodeDao,
    val queueDao: QueueDao,
    val podcastSettingsDao: PodcastSettingsDao,
    val json: Json,
) {

    private var refreshingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    fun loadPodcast(feedUrl: String): Flow<Podcast?> =
        podcastDao.getPodcastWithUrl(feedUrl)

    /**
     * updatePodcastReleaseDate
     */
    suspend fun updatePodcastReleaseDate(storePodcast: StorePodcast): Boolean {
        var needUpdate: Boolean = false
        val cachedPodcast = loadPodcast(storePodcast.feedUrl).firstOrNull()
        if (cachedPodcast != null) {
            if (cachedPodcast.releaseDateTime != storePodcast.releaseDateTime)
                needUpdate = true
            storePodcast.run {
                val metaData = PodcastItunesMetadata(
                    feedUrl = feedUrl,
                    podcastId = id,
                    artistId = artistId,
                    artistUrl = artistUrl,
                    category = category?.ordinal,
                    userRating = userRating.toDouble()
                )
                podcastDao.update(metaData)
            }
        }
        return needUpdate
    }

    /**
     * Update podcasts
     */
    suspend fun updatePodcasts(force: Boolean) {
        try {
            val feedUrls =
                podcastDao.getFollowedPodcasts().firstOrNull()?.map { it.feedUrl }.orEmpty()
            podcastsFetcher(feedUrls)
                .collect { (podcast, episodes) ->
                    insertPodcastAndEpisodes(podcast, episodes)
                }
        } catch (e: Throwable) {
            Timber.d("PodcastsRepository : podcastsFetcher(SampleFeeds).collect error: $e")
        }
    }

    /**
     * Update podcasts
     */
    @OptIn(InternalCoroutinesApi::class)
    suspend fun updatePodcast(feedUrl: String, currentPodcast: Podcast? = null) {
        val job = scope.async {
            try {
                Timber.d("PodcastFetcher : $feedUrl")
                // Now fetch the podcasts, and add each to each store
                podcastsFetcher.invoke(feedUrl, currentPodcast).collect { (podcast, episodes) ->
                    insertPodcastAndEpisodes(podcast, episodes)
                }
            } catch (e: Throwable) {
                Timber.d("PodcastsRepository : podcastsFetcher(SampleFeeds).collect error: $e")
                throw e
            }
        }
        job.await()
    }

    /**
     * Update podcasts
     */
    private suspend fun insertPodcastAndEpisodes(podcast: Podcast, episodes: List<Episode>) {
        val cachedPodcast = loadPodcast(podcast.feedUrl).firstOrNull()
        /*TODO: transaction */
        podcastDao.upsert(podcast)
        episodeDao.upsertAll(episodes)
    }

    /**
     * Update podcasts
     */
    @OptIn(InternalCoroutinesApi::class)
    suspend fun updatePodcastItunesMetadata(storePodcast: Podcast) {
        val job = scope.async {
            try {
                Timber.d("updatePodcastItunesMetadata: ${storePodcast.feedUrl}")
                val cachedPodcast = loadPodcast(storePodcast.feedUrl).firstOrNull()
                if (cachedPodcast != null) {
                    storePodcast.run {
                        val metaData = PodcastItunesMetadata(
                            feedUrl = feedUrl,
                            podcastId = podcastId,
                            artistId = artistId,
                            artistUrl = artistUrl,
                            category = category,
                            userRating = userRating
                        )
                        podcastDao.update(metaData)
                    }
                }
                else {
                    updatePodcast(feedUrl = storePodcast.feedUrl, currentPodcast = storePodcast)
                }
            } catch (e: Throwable) {
                Timber.d("PodcastsRepository : updatePodcastItunesMetadata error: $e")
                throw e
            }
        }
        job.await()
    }

    /**
     * Follow podcast
     */
    suspend fun followPodcast(feedUrl: String, updatePodcast: Boolean = false) {
        // fetch remote podcast data
        if (updatePodcast)
            updatePodcast(feedUrl)
        // subscribe to podcast
        podcastDao.followPodcast(feedUrl = feedUrl, followedAt = Clock.System.now())
        podcastSettingsDao.insert(PodcastSettings(feedUrl = feedUrl))
    }

    /**
     * Follow podcast
     */
    suspend fun followPodcast(storePodcast: StorePodcast, updatePodcast: Boolean = false) {
        // fetch remote podcast data
        if (updatePodcast)
            updatePodcast(storePodcast.feedUrl, storePodcast.podcast)
        // subscribe to podcast
        podcastDao.followPodcast(feedUrl = storePodcast.feedUrl, followedAt = Clock.System.now())
        podcastSettingsDao.insert(PodcastSettings(feedUrl = storePodcast.feedUrl))
    }

    /**
     * Unfollow podcast
     */
    suspend fun unfollowPodcast(feedUrl: String) {
        podcastDao.unfollowPodcast(feedUrl = feedUrl)
        val podcastPreferenceKeys = PodcastPreferenceKeys(feedUrl = feedUrl)
        dataStore.edit { preferences ->
            preferences.remove(podcastPreferenceKeys.notifications)
            preferences.remove(podcastPreferenceKeys.customPlaybackEffects)
            preferences.remove(podcastPreferenceKeys.customPlaybackSpeed)
            preferences.remove(podcastPreferenceKeys.trimSilence)
            preferences.remove(podcastPreferenceKeys.skipIntro)
            preferences.remove(podcastPreferenceKeys.skipEnding)
        }
    }

    fun getPodcastPreferences(feedUrl: String) {

    }
}