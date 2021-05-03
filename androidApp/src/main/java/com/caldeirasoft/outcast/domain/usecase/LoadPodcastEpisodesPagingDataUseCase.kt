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
    fun execute(podcast: Podcast): Flow<PagingData<Episode>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 200,
                prefetchDistance = 5
            ),
            initialKey = null,
            pagingSourceFactory = episodeDao.getEpisodesDataSourceWithUrl(podcast.feedUrl).asPagingSourceFactory()
        ).flow

}