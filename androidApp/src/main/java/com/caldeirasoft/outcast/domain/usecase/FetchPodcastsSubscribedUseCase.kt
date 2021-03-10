package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.PodcastSummary
import kotlinx.coroutines.flow.Flow

class FetchPodcastsSubscribedUseCase(val libraryRepository: LibraryRepository)
    : FlowUseCaseWithoutParams<List<PodcastSummary>> {
    override fun execute(): Flow<List<PodcastSummary>> =
        libraryRepository.loadAllPodcasts()
}