package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.data.api.ItunesAPI
import com.caldeirasoft.outcast.data.api.ItunesSearchAPI
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.util.PodcastsFetcher
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class PodcastsRepository @Inject constructor(
    private val podcastsFetcher: PodcastsFetcher,
    val itunesAPI: ItunesAPI,
    val searchAPI: ItunesSearchAPI,
    val database: Database,
    val context: Context,
    val dataStore: DataStore<Preferences>,
    val json: Json,
) {

    private var refreshingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    fun loadPodcast(feedUrl: String): Flow<Podcast?> =
        database.podcastQueries
            .getByUrl(feedUrl = feedUrl)
            .asFlow()
            .mapToOneOrNull()

    /**
     * updatePodcastReleaseDate
     */
    fun updatePodcastReleaseDate(feedUrl: String, podcastLookup: StorePodcast): Boolean {
        val podcastDb = database.podcastQueries.getByUrl(feedUrl).executeAsOneOrNull()
        return podcastDb?.let {
            if (podcastLookup.releaseDateTime == podcastDb.releaseDateTime) {
                database.podcastQueries.updateLastAccess(feedUrl)
                false
            } else {
                database.podcastQueries.updateMetadata(
                    podcastLookup.releaseDateTime,
                    podcastLookup.trackCount.toLong(),
                    feedUrl)
                true
            }
        } ?: true
    }

    /**
     * updatePodcastAndEpisodes
     */
    fun updatePodcastAndEpisodes(remotePodcast: StorePodcast) {
        database.transaction {
            database.podcastQueries.insert(remotePodcast.podcast)
            remotePodcast.episodes.onEach {
                database.episodeQueries.insert(it)
            }
        }
    }

    /**
     * addMostRecentEpisodeToInbox
     */
    fun addMostRecentEpisodeToInbox(feedUrl: String) {
        database.inboxQueries.addMostRecentEpisodeToInbox(feedUrl = feedUrl)
    }

    /**
     * addRecentEpisodesIntoInbox
     */
    fun addRecentEpisodesIntoInbox(feedUrl: String, releaseDateTime: Instant) {
        database.inboxQueries.addRecentEpisodesIntoInbox(
            feedUrl = feedUrl,
            releaseDateTime = releaseDateTime)
    }

    /**
     * addRecentEpisodesIntoQueueFirst
     */
    fun addRecentEpisodesIntoQueueFirst(feedUrl: String, releaseDateTime: Instant) {
        database.queueQueries.addRecentEpisodesIntoQueueFirst(
            feedUrl = feedUrl,
            releaseDateTime = releaseDateTime)
    }

    /**
     * addRecentEpisodesIntoQueueLast
     */
    fun addRecentEpisodesIntoQueueLast(feedUrl: String, releaseDateTime: Instant) {
        database.queueQueries.addRecentEpisodesIntoQueueLast(
            feedUrl = feedUrl,
            releaseDateTime = releaseDateTime)
    }

    /**
     * updateInboxEpisodeLimit
     */
    fun updateInboxEpisodeLimit(feedUrl: String, limit: Int) {
        database.inboxQueries.updateInboxEpisodeLimit(
            feedUrl = feedUrl,
            offset = limit.toLong())
    }

    /**
     * Update podcasts
     */
    suspend fun updatePodcasts(force: Boolean) {
        try {
            val feedUrls = database.podcastQueries
                .getSubscribed()
                .asFlow()
                .mapToList()
                .map { it.map { it.feedUrl } }
                .first()

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
        try {
            Timber.d("PodcastFetcher : $feedUrl")
            // Now fetch the podcasts, and add each to each store
            podcastsFetcher(feedUrl, currentPodcast).collect { (podcast, episodes) ->
                insertPodcastAndEpisodes(podcast, episodes)
            }
        } catch (e: Throwable) {
            Timber.d("PodcastsRepository : podcastsFetcher(SampleFeeds).collect error: $e")
            throw e
        }
    }

    /**
     * Update podcasts
     */
    private suspend fun insertPodcastAndEpisodes(podcast: Podcast, episodes: List<Episode>) {
        val cachedPodcast = loadPodcast(podcast.feedUrl).firstOrNull()
        database.transaction {
            database.podcastQueries.insert(podcast)
            episodes.onEach {
                database.episodeQueries.insert(it)
            }
        }

        cachedPodcast?.let {
            val podcastPreferenceKeys = PodcastPreferenceKeys(feedUrl = cachedPodcast.feedUrl)
            val settingsNewEpisodes =
                dataStore.data
                    .map { preferences -> preferences[podcastPreferenceKeys.newEpisodes] }
                    .firstOrNull()

            val settingsEpisodeLimit: Int =
                dataStore.data
                    .map { preferences -> preferences[podcastPreferenceKeys.episodeLimit] }
                    .firstOrNull()
                    ?.let { Integer.parseInt(it) }
                    ?: 0

            database.transaction {
                // add recent episodes to queue / inbox
                when (settingsNewEpisodes) {
                    NewEpisodesAction.INBOX.name -> {
                        addRecentEpisodesIntoInbox(
                            cachedPodcast.feedUrl,
                            cachedPodcast.releaseDateTime)
                        if (settingsEpisodeLimit != 0) {
                            updateInboxEpisodeLimit(
                                cachedPodcast.feedUrl,
                                settingsEpisodeLimit
                            )
                        }
                    }
                    NewEpisodesAction.QUEUE_FIRST.name ->
                        addRecentEpisodesIntoQueueFirst(
                            cachedPodcast.feedUrl,
                            cachedPodcast.releaseDateTime)
                    NewEpisodesAction.QUEUE_LAST.name ->
                        addRecentEpisodesIntoQueueLast(
                            cachedPodcast.feedUrl,
                            cachedPodcast.releaseDateTime)

                }
            }
        }
    }

    /**
     * Subscribe to podcast
     */
    suspend fun subscribe(feedUrl: String, updatePodcast: Boolean = false) {
        // fetch remote podcast data
        if (updatePodcast)
            updatePodcast(feedUrl)
        // subscribe to podcast
        database.podcastQueries.subscribe(feedUrl = feedUrl)
        addMostRecentEpisodeToInbox(feedUrl)
        val podcastPreferenceKeys = PodcastPreferenceKeys(feedUrl = feedUrl)
        dataStore.edit { preferences ->
            preferences[podcastPreferenceKeys.newEpisodes] = NewEpisodesAction.INBOX.name
            preferences[podcastPreferenceKeys.notifications] = true
            preferences[podcastPreferenceKeys.episodeLimit] = "0"
            preferences[podcastPreferenceKeys.customPlaybackEffects] = false
        }
    }

    /**
     * Unsubscribe from podcast
     */
    suspend fun unsubscribeFromPodcast(feedUrl: String) {
        database.podcastQueries
            .unsubscribe(feedUrl = feedUrl)
        val podcastPreferenceKeys = PodcastPreferenceKeys(feedUrl = feedUrl)
        dataStore.edit { preferences ->
            preferences.remove(podcastPreferenceKeys.newEpisodes)
            preferences.remove(podcastPreferenceKeys.notifications)
            preferences.remove(podcastPreferenceKeys.episodeLimit)
            preferences.remove(podcastPreferenceKeys.customPlaybackEffects)
            preferences.remove(podcastPreferenceKeys.customPlaybackSpeed)
            preferences.remove(podcastPreferenceKeys.trimSilence)
            preferences.remove(podcastPreferenceKeys.skipIntro)
            preferences.remove(podcastPreferenceKeys.skipEnding)
        }
    }
}