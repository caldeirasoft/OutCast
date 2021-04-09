package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class LoadSettingsUseCase(
    val dataStoreRepository: DataStoreRepository,
) {
    val settings: Flow<Preferences> =
        dataStoreRepository.dataStore.data
}