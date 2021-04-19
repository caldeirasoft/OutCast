package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.util.Resource
import com.caldeirasoft.outcast.domain.util.networkBoundResource
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityScoped
class FetchStoreEpisodeDataUseCase @Inject constructor(
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