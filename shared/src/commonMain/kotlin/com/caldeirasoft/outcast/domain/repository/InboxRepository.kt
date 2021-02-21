package com.caldeirasoft.outcast.domain.repository;

import com.caldeirasoft.outcast.domain.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

interface InboxRepository {

    /**
     * Select all episodes from inbox
     */
    fun fetchEpisodes(): Flow<List<EpisodeSummary>>

    /**
     * Select favorites episodes from a podcast
     */
    fun fetchEpisodesByGenre(genreId: Int): Flow<List<EpisodeSummary>>

    /**
     * Select favorites episodes count by podcasts
     */
    fun fetchGenreIds(): Flow<List<Int>>

    /**
     * Insert episode into Inbox
     */
    fun addToInbox(episode: Episode)

    /**
     * Remove episode from Inbox by id
     */
    fun removeFromInbox(episodeId: Long)

    /**
     * Delete all episodes
     */
    fun deleteAll()
}