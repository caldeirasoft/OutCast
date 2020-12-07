package com.caldeirasoft.outcast.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.datastore.preferences.protobuf.InvalidProtocolBufferException
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.dto.StoreFrontDto
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.interfaces.StoreItemFeatured
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.repository.DataStoreRepository
import com.caldeirasoft.outcast.domain.repository.LocalCacheRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import kotlinx.serialization.serializer
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.security.InvalidKeyException
import java.util.*

class LocalCacheRepositoryImpl(val context: Context)
    : LocalCacheRepository {
    companion object {
        const val CACHE_STORE_FILE_NAME = "cache.db"
    }

    private object PreferenceKeys {
        val DIRECTORY = preferencesKey<String>("directory")
    }

    // Build the DataStore
    private val cacheDataStore: DataStore<Preferences> =
        context.createDataStore(name = CACHE_STORE_FILE_NAME)

    private val preferencesFlow = cacheDataStore.data
        .catch { exception ->
            if (exception is IOException)
                emit(emptyPreferences())
            else throw exception
        }

    override val directory: Flow<StoreDirectory?>
        = preferencesFlow
        .map { preferences -> preferences[PreferenceKeys.DIRECTORY] }
        .map {
            val module = SerializersModule {
                polymorphic(StoreCollection::class) {
                    subclass(StoreCollectionFeatured::class)
                    subclass(StoreCollectionPodcasts::class)
                    subclass(StoreCollectionRooms::class)
                    subclass(StoreCollectionEpisodes::class)
                }
                polymorphic(StoreItemWithArtwork::class) {
                    subclass(StorePodcastFeatured::class)
                    subclass(StoreRoomFeatured::class)
                    subclass(StoreRoom::class)
                    subclass(StorePodcast::class)
                }
            }
            val format = Json {
                serializersModule = module
            }
            it?.let {
                val storeData: StoreDirectory = format.decodeFromString(serializer(), it)
                storeData
            }
        }

    override suspend fun saveDirectory(storeData: StoreDirectory) {
        cacheDataStore.edit { preferences ->
            val module = SerializersModule {
                polymorphic(StoreCollection::class) {
                    subclass(StoreCollectionFeatured::class)
                    subclass(StoreCollectionPodcasts::class)
                    subclass(StoreCollectionRooms::class)
                    subclass(StoreCollectionEpisodes::class)
                }
                polymorphic(StoreItemWithArtwork::class) {
                    subclass(StorePodcastFeatured::class)
                    subclass(StoreRoomFeatured::class)
                    subclass(StoreRoom::class)
                    subclass(StorePodcast::class)
                }
            }
            val format = Json {
                serializersModule = module
            }
            preferences[PreferenceKeys.DIRECTORY] = format.encodeToString(serializer(), storeData)
        }
    }
}