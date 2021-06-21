package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.entities.EpisodeWithPodcast
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LoadEpisodeFromDbUseCase @Inject constructor(
    val podcastDao: PodcastDao,
    val episodeDao: EpisodeDao
) {
    fun execute(feedUrl: String, guid: String): Flow<EpisodeWithPodcast?> =
        episodeDao.getEpisodeWithGuid(feedUrl, guid)
}