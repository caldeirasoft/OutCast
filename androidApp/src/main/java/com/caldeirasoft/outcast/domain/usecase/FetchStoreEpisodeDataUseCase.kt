package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.EpisodeWithInfos
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import kotlinx.coroutines.flow.Flow

class FetchStoreEpisodeDataUseCase constructor(
    val storeRepository: StoreRepository,
    val libraryRepository: LibraryRepository
) {
    fun execute(episode: Episode, storeFront: String) : Flow<Resource<EpisodeWithInfos>> =
        networkBoundResource(
            loadFromDb = { libraryRepository.loadEpisode(episode.episodeId) },
            shouldFetch = { it == null },
            fetchFromRemote = {
                storeRepository.getPodcastDataAsync(episode.url, storeFront)
            },
            saveRemoteData = { libraryRepository.updatePodcastAndEpisodes(it) }
        )
}