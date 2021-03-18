package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.Podcast
import kotlinx.coroutines.flow.Flow

class FetchPodcastsSubscribedUseCase(val libraryRepository: LibraryRepository) :
    FlowUseCaseWithoutParams<List<Podcast>> {
    override fun execute(): Flow<List<Podcast>> =
        libraryRepository.loadAllPodcasts()
}