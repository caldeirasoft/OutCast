package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import javax.inject.Inject

class RemoveSaveEpisodeUseCase @Inject constructor(
    val episodeDao: EpisodeDao
) {
    suspend fun execute(episode: Episode) {
        episodeDao.deleteFromLibrary(episode.feedUrl, episode.guid)
    }
}