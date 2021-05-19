package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.db.dao.EpisodeDao
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.enums.SortOrder
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class LoadPodcastEpisodesPagingDataUseCase @Inject constructor(
    val episodeDao: EpisodeDao,
    val dataStoreRepository: DataStoreRepository
) {
    fun execute(feedUrl: String, sortOrder: SortOrder): Flow<PagingData<Episode>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                maxSize = 4000,
                prefetchDistance = 5
            ),
            initialKey = null,
            pagingSourceFactory = if (sortOrder == SortOrder.DESC)
                episodeDao.getEpisodesDataSourceWithUrl(feedUrl).asPagingSourceFactory()
                else episodeDao.getEpisodesDataSourceWithUrlOrderByDateAsc(feedUrl).asPagingSourceFactory()
        ).flow
    }
}