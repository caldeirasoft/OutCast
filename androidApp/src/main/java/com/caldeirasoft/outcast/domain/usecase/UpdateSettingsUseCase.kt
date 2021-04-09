package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.data.repository.DataStoreRepository

class UpdateSettingsUseCase(
    val dataStoreRepository: DataStoreRepository,
) {
    suspend fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        dataStoreRepository.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}