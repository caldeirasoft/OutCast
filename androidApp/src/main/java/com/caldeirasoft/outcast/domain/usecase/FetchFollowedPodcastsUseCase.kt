package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.entities.Podcast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FetchFollowedPodcastsUseCase @Inject constructor(val podcastDao: PodcastDao) {
    fun getFollowedPodcasts(): Flow<List<Podcast>> =
        podcastDao.getFollowedPodcasts()

    fun getFollowedPodcastIds(): Flow<List<Long>> =
        podcastDao.getFollowedPodcastIds()
}