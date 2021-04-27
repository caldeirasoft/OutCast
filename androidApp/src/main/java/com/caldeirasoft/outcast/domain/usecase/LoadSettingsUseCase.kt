package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.common.PodcastPreferences
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoadSettingsUseCase @Inject constructor(
    val dataStoreRepository: DataStoreRepository,
) {
    val dataStore: DataStore<Preferences> =
        dataStoreRepository.dataStore

    val settings: Flow<Preferences> =
        dataStoreRepository.dataStore.data
}