package com.caldeirasoft.outcast.domain.usecase

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.common.PodcastPreferences
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.repository.DataStoreRepository
import javax.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    val dataStoreRepository: DataStoreRepository,
) {
    /*
    suspend fun updatePodcastNewEpisodes(feedUrl: String, newEpisodesAction: NewEpisodesAction) {
        dataStoreRepository.dataStore.edit { preferences ->
            val podcastPreferenceKeys = PodcastPreferenceKeys(feedUrl = feedUrl)
            preferences[podcastPreferenceKeys.newEpisodes] = newEpisodesAction.name
        }
    }
    */

    suspend fun updatePodcastNotifications(feedUrl: String, notifications: Boolean) {
        dataStoreRepository.dataStore.edit { preferences ->
            val podcastPreferenceKeys = PodcastPreferenceKeys(feedUrl = feedUrl)
            preferences[podcastPreferenceKeys.notifications] = notifications
        }
    }

    suspend inline fun <reified T> updatePreference(key: Preferences.Key<T>, value: T) {
        dataStoreRepository.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }
}