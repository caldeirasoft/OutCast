package com.caldeirasoft.outcast.data.repository

import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import com.caldeirasoft.outcast.domain.util.Resources
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import java.io.IOException
import java.security.InvalidKeyException
import java.util.*

class DataStoreRepositoryJvm(settings: FlowSettings)
    : DataStoreRepository(settings) {
    private object PreferenceKeys {
        val STOREFRONT_REGION = "store_front"
        val LAST_SYNC = "last_sync"
    }

    override val storeCountry: Flow<String>
            = settings.getStringFlow(PreferenceKeys.STOREFRONT_REGION, "FR")
    //context.resources.configuration.locales.get(0).country

    override val lastSyncDate: Flow<Long>
            = settings.getLongFlow(PreferenceKeys.LAST_SYNC, -1)

    override suspend fun saveLastSyncDate() {
        settings.putLong(PreferenceKeys.LAST_SYNC, Calendar.getInstance().timeInMillis)
    }

    /**
     * getCurrentStoreFront
     */
    override fun getCurrentStoreFront(country: String): String {
        val storeFronts = getStoreFronts()
        val countriesMap = storeFronts
            .storeFronts
            .map { it.countryCode to it }
            .toMap()
        val languageMap = storeFronts
            .languages
            .map { it.name to it.id }
            .toMap()

        val contextLocale = Locale.getDefault()
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

        val storeLanguageId = currentLanguage ?: defaultLanguage ?: throw Exception("language not found")
        return "${selectedCountry.id}-${storeLanguageId},29"
    }
}
