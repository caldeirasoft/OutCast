package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.repository.LibraryRepository
import com.caldeirasoft.outcast.db.EpisodeWithInfos
import kotlinx.coroutines.flow.Flow

class LoadEpisodeUseCase(val libraryRepository: LibraryRepository)
    : FlowUseCase<Long, EpisodeWithInfos> {
    override fun execute(param: Long): Flow<EpisodeWithInfos> {
        return libraryRepository.loadEpisode(param)
    }
}