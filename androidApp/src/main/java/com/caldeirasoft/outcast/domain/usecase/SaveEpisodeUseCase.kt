package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import javax.inject.Inject

class SaveEpisodeUseCase @Inject constructor(
    val episodeDao: EpisodeDao
) {
    suspend fun execute(episode: Episode) {
        episodeDao.saveEpisodeToLibrary(episode.feedUrl, episode.guid)
    }
}