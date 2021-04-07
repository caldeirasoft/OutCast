package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.NewEpisodesAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.security.InvalidKeyException
import java.util.*

class DataStoreRepository(val context: Context) {
    private object PreferenceKeys {
        val STOREFRONT_REGION = stringPreferencesKey("store_front")
        val LAST_SYNC = longPreferencesKey("last_sync")
    }

    // Build the DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    val dataStore: DataStore<Preferences> = context.dataStore

    val storeCountry: Flow<String> = context.dataStore.data
        .map { preferences ->
            Timber.d("DBG - storeCountry")
            preferences[PreferenceKeys.STOREFRONT_REGION] ?: "FR"
            //context.resources.configuration.locales.get(0).country
        }

    suspend fun saveStoreCountryPreference(country: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.STOREFRONT_REGION] = country
        }
    }

    val lastSyncDate: Flow<Long> = context.dataStore.data
        .map { preferences -> preferences[PreferenceKeys.LAST_SYNC] ?: -1 }

    suspend fun saveLastSyncDate() {
        context.dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_SYNC] = Calendar.getInstance().timeInMillis
        }
    }

    suspend fun savePodcastSetting(podcastId: Long, newEpisodeAction: NewEpisodesAction) {
        val prefNewEpisodesKey = stringPreferencesKey("$podcastId:pref_new_episodes")
        context.dataStore.edit { preferences ->
            preferences[prefNewEpisodesKey] = newEpisodeAction.name
        }
    }

    /**
     * getCurrentStoreFront
     */
    fun getCurrentStoreFront(country: String): String {
        val storeFronts = getStoreFronts()
        val countriesMap = storeFronts
            .storeFronts
            .map { it.countryCode to it }
            .toMap()
        val languageMap = storeFronts
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

        return "143441-1,29"
        return storeFront
    }

    /**
     * getStoreFront
     */
    private fun getStoreFronts(): com.caldeirasoft.outcast.domain.dto.StoreFrontDto {
        val text = context.resources
            .openRawResource(R.raw.store_fronts)
            .bufferedReader()
            .use { it.readText() }

        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        return nonStrictJson.decodeFromString(com.caldeirasoft.outcast.domain.dto.StoreFrontDto.serializer(), text)
    }
}