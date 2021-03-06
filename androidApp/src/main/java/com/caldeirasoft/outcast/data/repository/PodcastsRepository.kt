package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.dao.*
import com.caldeirasoft.outcast.data.db.entities.*
import com.caldeirasoft.outcast.data.db.entities.Settings.Companion.episodeLimitOption
import com.caldeirasoft.outcast.data.util.PodcastsFetcher
import com.caldeirasoft.outcast.domain.enums.NewEpisodesOptions
import com.caldeirasoft.outcast.domain.enums.PodcastEpisodeLimitOptions
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class PodcastsRepository @Inject constructor(
    private val podcastsFetcher: PodcastsFetcher,
    val context: Context,
    val dataStore: DataStore<Preferences>,
    val podcastDao: PodcastDao,
    val episodeDao: EpisodeDao,
    val inboxDao: InboxDao,
    val queueDao: QueueDao,
    val settingsDao: SettingsDao,
    val podcastSettingsDao: PodcastSettingsDao,
    val json: Json,
) {

    private var refreshingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Get followed podcasts
     */
    fun getFollowedPodcasts(): Flow<List<Podcast>> =
        podcastDao.getFollowedPodcasts()

    /**
     * Get followed podcast ids
     */
    fun getFollowedPodcastIds(): Flow<List<Long>> =
        podcastDao.getFollowedPodcastIds()

    /**
     * Get single podcast with url
     */
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
                    genre = genre?.name,
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
        podcastDao.updatePodcastReleaseDate(podcast.feedUrl, podcast.releaseDateTime)

        val podcastSettings = podcastSettingsDao.getPodcastSettingsWithUrl(podcast.feedUrl).firstOrNull()
        val settings = settingsDao.getAllSettings().filterNotNull().first();
        if (podcastSettings != null) {
            val episodeLimit = podcastSettings.episodeLimitOption
                .takeUnless { it == PodcastEpisodeLimitOptions.DEFAULT_SETTING }
                ?: settings.episodeLimitOption
            when(podcastSettings.newEpisodesOption) {
                NewEpisodesOptions.ADD_TO_INBOX ->
                    inboxDao.deleteEpisodesWithUrlAndLimit(podcast.feedUrl, episodeLimit.ordinal)
                NewEpisodesOptions.ADD_TO_QUEUE_LAST,
                NewEpisodesOptions.ADD_TO_QUEUE_NEXT ->
                    queueDao.deleteEpisodesWithUrlAndLimit(podcast.feedUrl, episodeLimit.ordinal)
                else -> Unit
            }
        }
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
                            genre = genre,
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
        inboxDao.addMostRecentEpisode(feedUrl)
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
        inboxDao.addMostRecentEpisode(storePodcast.feedUrl)
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

    /**
     * update podcast sortOrder
     */
    fun updatePodcastSortOrder(feedUrl: String, sortOrder: SortOrder) {
        scope.launch {
            podcastDao.updateSortOrder(feedUrl, sortOrder.ordinal)
        }
    }

    /**
     * update podcast filter
     */
    suspend fun updatePodcastFilter(feedUrl: String, filter: PodcastFilter) {
        podcastDao.updateFilter(feedUrl, filter.ordinal)
    }


    /**
     * addMostRecentEpisodeToInbox
     */
    fun addMostRecentEpisodeToInbox(feedUrl: String) {
        scope.launch {
            inboxDao.addMostRecentEpisode(feedUrl = feedUrl)
        }
    }

    fun getPodcastPreferences(feedUrl: String) {

    }
}