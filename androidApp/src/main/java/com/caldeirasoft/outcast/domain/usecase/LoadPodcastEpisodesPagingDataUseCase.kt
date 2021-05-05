package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LoadPodcastEpisodesPagingDataUseCase @Inject constructor(
    val episodeDao: EpisodeDao
) {
    fun execute(feedUrl: String): Flow<PagingData<Episode>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 4000,
                prefetchDistance = 5
            ),
            initialKey = null,
            pagingSourceFactory = episodeDao.getEpisodesDataSourceWithUrl(feedUrl).asPagingSourceFactory()
        ).flow

}