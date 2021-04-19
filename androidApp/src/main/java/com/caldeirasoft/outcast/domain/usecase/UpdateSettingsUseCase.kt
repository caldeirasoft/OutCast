package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    val dataStoreRepository: DataStoreRepository,
) {
    suspend fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        dataStoreRepository.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}