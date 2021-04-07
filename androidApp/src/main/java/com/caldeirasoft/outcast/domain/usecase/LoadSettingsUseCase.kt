package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.repository.DataStoreRepository

class LoadSettingsUseCase(
    val dataStoreRepository: DataStoreRepository,
) {

    val settings: DataStore<Preferences> =
        dataStoreRepository.dataStore
}