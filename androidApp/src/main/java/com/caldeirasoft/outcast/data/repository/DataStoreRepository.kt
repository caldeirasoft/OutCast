package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.security.InvalidKeyException
import java.util.*
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    val context: Context,
    val dataStore: DataStore<Preferences>,
) {
    private object PreferenceKeys {
        val STOREFRONT_REGION = stringPreferencesKey("store_front")
        val LAST_SYNC = longPreferencesKey("last_sync")
    }

    val storeCountry: Flow<String> = dataStore.data
        .map { preferences ->
            Timber.d("DBG - storeCountry")
            preferences[PreferenceKeys.STOREFRONT_REGION] ?: "FR"
            //context.resources.configuration.locales.get(0).country
        }

    suspend fun saveStoreCountryPreference(country: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.STOREFRONT_REGION] = country
        }
    }

    val lastSyncDate: Flow<Long> = dataStore.data
        .map { preferences -> preferences[PreferenceKeys.LAST_SYNC] ?: -1 }

    suspend fun saveLastSyncDate() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_SYNC] = Calendar.getInstance().timeInMillis
        }
    }


    suspend fun <T> updatePreference(key: Preferences.Key<T>, value: T) {
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    val storeFront: Flow<String> =
        storeCountry
            .map { getCurrentStoreFront(it) }

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
    private fun getStoreFronts(): StoreFrontDto {
        val text = context.resources
            .openRawResource(R.raw.store_fronts)
            .bufferedReader()
            .use { it.readText() }

        val nonStrictJson = Json { isLenient = true; ignoreUnknownKeys = true }
        return nonStrictJson.decodeFromString(StoreFrontDto.serializer(), text)
    }
}