package com.caldeirasoft.outcast.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.data.util.PodcastRemoteMediator
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.db.Podcast
import kotlinx.coroutines.flow.Flow

class LoadPodcastEpisodesPagingDataUseCase(
    val storeRepository: StoreRepository,
    val libraryRepository: LibraryRepository
) {
    @OptIn(ExperimentalPagingApi::class)
    fun execute(podcast: Podcast, storeFront: String): Flow<PagingData<EpisodeSummary>> {
        val mediator = PodcastRemoteMediator(
            podcast = podcast,
            storeFront = storeFront,
            storeRepository = storeRepository,
            libraryRepository = libraryRepository
        )
        val pagingSourceFactory =
            libraryRepository.getEpisodesByPodcastIdPagingSourceFactory(podcastId = podcast.podcastId)

        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            remoteMediator = mediator,
            pagingSourceFactory = { pagingSourceFactory.invoke() }
        ).flow
    }
}