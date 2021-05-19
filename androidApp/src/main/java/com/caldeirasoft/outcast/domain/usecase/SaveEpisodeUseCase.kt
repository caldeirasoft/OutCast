package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.common.PodcastPreferences
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import javax.inject.Inject

class SaveEpisodeUseCase @Inject constructor(
    val episodeDao: EpisodeDao
) {
    suspend fun execute(episode: Episode) {
        episodeDao.saveEpisodeToLibrary(episode.feedUrl, episode.guid)
    }
}