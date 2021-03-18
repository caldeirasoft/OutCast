package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.Database
import com.caldeirasoft.outcast.db.Episode
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

class QueueRepository(val database: Database) {
    fun fetchQueue(): Flow<List<Episode>> =
        database.queueQueries
            .selectAll()
            .asFlow()
            .mapToList()

    fun addToQueue(episode: Episode, queueIndex: Long) {
        database.queueQueries
            addToQueue(episode = episode, queueIndex = queueIndex)
    }

    fun addToQueueNext(episode: Episode) {
        database.queueQueries
            addToQueueNext(episode = episode)
    }

    fun addToQueueLast(episode: Episode) {
        database.queueQueries
            addToQueueLast(episode = episode)
    }

    fun removeFromQueue(id: Long) {
        database.queueQueries
            removeFromQueue(id = id)
    }


}
