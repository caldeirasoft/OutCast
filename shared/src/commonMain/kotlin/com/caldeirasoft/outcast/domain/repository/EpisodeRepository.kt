package com.caldeirasoft.outcast.domain.repository;

import com.caldeirasoft.outcast.domain.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

interface EpisodeRepository {

    /**
     * Select all episodes by podcast id
     */
    fun fetchEpisodesByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>>

    /**
     * Select all favorites episodes
     */
    fun fetchEpisodesFavorites(): Flow<List<EpisodeSummary>>

    /**
     * Select all played episodes
     */
    fun fetchEpisodesHistory(): Flow<List<EpisodeSummary>>

    /**
     * Select favorites episodes from a podcast
     */
    fun fetchEpisodesFavoritesByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>>

    /**
     * Select all played episodes from a podcast
     */
    fun fetchEpisodesHistoryByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>>

    /**
     * Select inbox episodes by podcast id
     */
    fun fetchEpisodesFromInboxByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>>

    /**
     * Select queue episodes by podcast id
     */
    fun fetchEpisodesFromQueueByPodcastId(podcastId: Long): Flow<List<EpisodeSummary>>

    /**
     * Select favorites episodes count by podcasts
     */
    fun fetchCountEpisodesFavoritesByPodcast(): Flow<List<EpisodesCountByPodcast>>

    /**
     * Select played episodes count by podcasts
     */
    fun fetchCountEpisodesPlayedByPodcast(): Flow<List<EpisodesCountByPodcast>>

    /**
     * fetch episodes from by section
     */
    fun fetchCountEpisodesBySection(podcastId: Long): Flow<SectionWithCount>

    /**
     * Select an episode by id
     */
    fun getEpisode(episodeId: Long): Flow<EpisodeWithInfos>

    /**
     * Insert episode
     */
    fun insertEpisode(episode: Episode)

    /**
     * Insert episodes
     */
    fun insertEpisodes(list: List<Episode>)

    /**
     * Mark an episode as played
     */
    fun markEpisodeAsPlayed(episodeId: Long)

    /**
     * Mark an episode as unplayed
     */
    fun markEpisodeAsUnplayed(episodeId: Long)

    /**
     * Update episode playback position
     */
    fun updateEpisodePlaybackPosition(episodeId: Long, playbackPosition: Long?)

    /*
     * Update episode favorite status
     */
    fun updateEpisodeFavoriteStatus(episodeId: Long, isFavorite: Boolean)

    /**
     * Delete episode by id
     */
    fun deleteEpisodeById(episodeId: Long)

    /**
     * Delete all episodes
     */
    fun deleteAll()
}