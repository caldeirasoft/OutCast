package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.days

class LoadLatestEpisodeCategoriesUseCase @Inject constructor(
    private val episodeDao: EpisodeDao
) {
    fun getLatestEpisodesCategories(): Flow<List<Category?>> =
        episodeDao.getLatestEpisodesCategories()
}