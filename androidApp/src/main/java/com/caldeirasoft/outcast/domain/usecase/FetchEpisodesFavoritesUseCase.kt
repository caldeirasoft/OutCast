package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.Episode
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class FetchEpisodesFavoritesUseCase @Inject constructor(
    private val libraryRepository: LibraryRepository,
) : FlowUseCaseWithoutParams<List<Episode>> {
    override fun execute() = libraryRepository.loadEpisodesFavorites()
}