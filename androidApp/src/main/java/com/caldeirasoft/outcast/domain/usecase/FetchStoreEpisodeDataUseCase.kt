package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow

class FetchStoreEpisodeDataUseCase constructor(
    val storeRepository: StoreRepository,
    val podcastsRepository: PodcastsRepository,
    val libraryRepository: LibraryRepository,
) {
    fun execute(episode: Episode, storeFront: String): Flow<Resource<Episode>> =
        networkBoundResource(
            loadFromDb = { libraryRepository.loadEpisode(episode) },
            shouldFetch = { it == null },
            fetchFromRemote = {
                podcastsRepository.updatePodcast(episode.feedUrl)
                true
            },
            saveRemoteData = {

            }
        )
}