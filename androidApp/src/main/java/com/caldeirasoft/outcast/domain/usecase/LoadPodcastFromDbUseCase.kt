package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.entities.Podcast
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LoadPodcastFromDbUseCase @Inject constructor(
    val podcastDao: PodcastDao,
) {
    fun execute(feedUrl: String): Flow<Podcast?> =
        podcastDao.getPodcastWithUrl(feedUrl)
}