package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.data.db.dao.QueueDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import kotlinx.coroutines.flow.Flow

class QueueRepository(val queueDao: QueueDao) {
    fun fetchQueue(): Flow<List<Episode>> =
        queueDao.getEpisodes()

    suspend fun addToQueue(episode: Episode, queueIndex: Int) {
        queueDao.addToQueue(feedUrl = episode.feedUrl, guid = episode.guid, queueIndex = queueIndex)
    }

    suspend fun addToQueueNext(episode: Episode) {
        queueDao.addToQueueNext(feedUrl = episode.feedUrl, guid = episode.guid)
    }

    suspend fun addToQueueLast(episode: Episode) {
        queueDao.addToQueueLast(feedUrl = episode.feedUrl, guid = episode.guid)
    }

    suspend fun removeFromQueue(episode: Episode) {
        queueDao.removeFromQueue(feedUrl = episode.feedUrl, guid = episode.guid)
    }


}
