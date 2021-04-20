package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.Podcast
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadFollowedPodcastsUseCase @Inject constructor(val libraryRepository: LibraryRepository) {
    fun execute(): Flow<List<Podcast>> =
        libraryRepository.loadFollowedPodcasts()
}