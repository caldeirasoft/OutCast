package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.navigation.NavBackStackEntry
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.dao.PodcastSettingsDao
import com.caldeirasoft.outcast.data.db.dao.SettingsDao
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import timber.log.Timber
import java.net.URLDecoder
import java.security.InvalidKeyException
import java.util.*
import javax.inject.Inject

class SettingsRepository @Inject constructor(
    val context: Context,
    val settingsDao: SettingsDao,
    val podcastSettingsDao: PodcastSettingsDao
) {
    private val storeFrontList = getStoreFronts()

    /**
     * getStoreFront
     */
    private fun getStoreFronts(): StoreFrontDto {
        val text = context.resources
            .openRawResource(R.raw.store_fronts)
            .bufferedReader()
            .use { it.readText() }

        return text.unserialize()
    }

    inline fun <reified T> String.unserialize(): T {
        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        return nonStrictJson.decodeFromString(serializer(), URLDecoder.decode(this, "UTF-8"))
    }

    /**
     * Get all settings
     */
    fun getSettings(): Flow<Settings> =
        settingsDao
            .getAllSettings()
            .filterNotNull()

    /**
     * Get store country
     */
    val storeCountryFlow: Flow<String> =
        getSettings()
            .map { it.storeCountry }
            .distinctUntilChanged()

    /**
     * Get store front
     */
    val storeFrontFlow: Flow<String> =
        getSettings()
            .map { it.storeCountry }
            .map { getCurrentStoreFront(it) }
            .distinctUntilChanged()

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

    /**
     * getCurrentStoreFront
     */
    fun getCurrentStoreFront(country: String): String {
        val countriesMap = storeFrontList
            .storeFronts
            .map { it.countryCode to it }
            .toMap()
        val languageMap = storeFrontList
            .languages
            .map { it.name to it.id }
            .toMap()

        val contextLocale = context.resources.configuration.locales.get(0)
        val selectedCountry =
            countriesMap[country] ?: throw IllegalArgumentException("country not found")
        var defaultLanguage:String? = null
        var currentLanguage:String? = null
        // default language : english
        selectedCountry
            .languages
            .forEach { lang ->
                val locale = Locale.forLanguageTag(lang.replace('_', '-'))
                if (locale.language == Locale.ENGLISH.language)
                    languageMap[lang]?.let {
                        defaultLanguage = it
                    }
                if (locale.language == contextLocale.language)
                    languageMap[lang]?.let {
                        currentLanguage = it
                    }
            }

        val storeLanguageId = currentLanguage ?: defaultLanguage ?: throw InvalidKeyException("language not found")
        val storeFront = "${selectedCountry.id}-${storeLanguageId},29"

        return storeFront
    }
}