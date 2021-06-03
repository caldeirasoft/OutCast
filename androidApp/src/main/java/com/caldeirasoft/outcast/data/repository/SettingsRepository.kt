package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.dao.PodcastSettingsDao
import com.caldeirasoft.outcast.data.db.dao.SettingsDao
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.security.InvalidKeyException
import java.util.*
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    val context: Context,
    val settingsDao: SettingsDao,
    val podcastSettingsDao: PodcastSettingsDao
) {
    /**
     * Get all settings
     */
    fun getSettings(): Flow<Settings> =
        settingsDao.getAllSettings()

    /**
     * Update settings
     */
    suspend fun updateSettings(settings: Settings) {
        settingsDao.update(settings)
    }

    /**
     * Get podcast settings
     */
    fun getPodcastSettings(feedUrl: String): Flow<PodcastSettings> =
        podcastSettingsDao
            .getPodcastSettingsWithUrl(feedUrl)
            .filterNotNull()

    /**
     * Insert podcast settings
     */
    suspend fun insertPodcastSettings(settings: PodcastSettings) {
        podcastSettingsDao.insert(settings)
    }

    /**
     * Update podcast settings
     */
    suspend fun updatePodcastSettings(settings: PodcastSettings) {
        podcastSettingsDao.update(settings)
    }

    /**
     * Delete podcast settings
     */
    suspend fun deletePodcastSettings(settings: PodcastSettings) {
        podcastSettingsDao.delete(settings)
    }
}