package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import com.caldeirasoft.outcast.data.repository.LibraryRepository

class UnsubscribeUseCase constructor(
    val podcastRepository: LibraryRepository,
    val dataStoreRepository: DataStoreRepository,
) {

    suspend fun execute(podcastId: Long) {
        podcastRepository.unsubscribeFromPodcast(podcastId = podcastId)
        dataStoreRepository.dataStore.edit { preferences ->
            //preferences.remove()
        }
    }
}