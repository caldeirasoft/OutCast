package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.EpisodeSummary

class FetchEpisodesFavoritesUseCase(
    private val libraryRepository: LibraryRepository
) : FlowUseCaseWithoutParams<List<EpisodeSummary>> {
    override fun execute() = libraryRepository.loadEpisodesFavorites()
}