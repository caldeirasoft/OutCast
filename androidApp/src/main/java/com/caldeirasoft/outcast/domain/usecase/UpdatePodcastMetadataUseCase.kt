package com.caldeirasoft.outcast.domain.usecase

import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.PodcastsRepository
import com.caldeirasoft.outcast.data.repository.StoreRepository
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.util.Resource
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.time.hours

class UpdatePodcastMetadataUseCase @Inject constructor(
    val podcastsRepository: PodcastsRepository,
) {
    fun execute(podcast: Podcast): Flow<Boolean> = flow {
        podcastsRepository.updatePodcastItunesMetadata(podcast)
        emit(true)
    }
}