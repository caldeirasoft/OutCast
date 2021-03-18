package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.Episode

class FetchEpisodesFavoritesUseCase(
    private val libraryRepository: LibraryRepository,
) : FlowUseCaseWithoutParams<List<Episode>> {
    override fun execute() = libraryRepository.loadEpisodesFavorites()
}