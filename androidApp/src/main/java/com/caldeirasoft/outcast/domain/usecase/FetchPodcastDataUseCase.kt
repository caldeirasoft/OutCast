package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class FetchPodcastDataUseCase @Inject constructor(
    val podcastsRepository: PodcastsRepository,
) {
    fun execute(podcast: Podcast): Flow<Boolean> = flow {
        podcastsRepository.updatePodcastItunesMetadata(podcast)
        emit(true)
    }
}