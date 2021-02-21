package com.caldeirasoft.outcast.domain.repository;

import com.caldeirasoft.outcast.domain.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Created by Edmond on 15/02/2018.
 */

interface QueueRepository {

    /**
     * Select all episodes from queue
     */
    fun fetchQueue(): Flow<List<EpisodeSummary>>

    /**
     * Insert episode into queue
     */
    fun addToQueue(episode: Episode, queueIndex: Long)

    /**
     * Insert episode into queue
     */
    fun addToQueueNext(episode: Episode)

    /**
     * Insert episode into queue
     */
    fun addToQueueLast(episode: Episode)

    /**
     * Delete episode from queue by id
     */
    fun removeFromQueue(id: Long)
}