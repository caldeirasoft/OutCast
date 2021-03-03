package com.caldeirasoft.outcast.data.util.local

import com.caldeirasoft.outcast.data.dto.StoreFrontDto
import com.squareup.moshi.Moshi
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlinx.serialization.serializerOrNull
import timber.log.Timber
import java.io.File
import java.io.IOException
import kotlin.time.Duration

/**
 * Naive caching implementation for the JVM that uses WeakReference
 * as a cache "buffer" that can be cleaned up by the GC when necessary.
 */
class DiskCache(
    //val config: GitrestConfig,
    val jsonConfig: Json,
    appCacheDir: File,
) : Cache {

    val cacheDir = File(appCacheDir, "http")

    fun String.cacheFile() = File(cacheDir, "${this.replace(File.separator, "_")}.json")

    override suspend fun set(key: String, value: Any) {
        // obtain a serializer + type for the value (this is all just a ridiculous hack)
        return
        val string: String = encodeToString(value)

        try {
            cacheDir.mkdirs()
            key.cacheFile().writeText(string)
        } catch (e: IOException) {
            Timber.e("GIT-REST: ${e::class.simpleName} - ${e.message}")
        }
    }

    override suspend fun <T : Any> getEntry(key: String, timeLimit: Duration, useEntryEvenIfExpired: Boolean): Entry<T>? {
        return try {
            // check contents; destructure file parts if safe
            val fileContents = key.cacheFile().readText().split("#", limit = 3)
            if (fileContents.size != 3) return null
            val (className, lastModified, json) = fileContents

            val isExpired = hasExpired(lastModified.toLong(), timeLimit)
            return null

            if (!isExpired || useEntryEvenIfExpired)
            {
                decodeFromString<T>(className, json)?.let {
                    Entry(it, lastModified.toLong(), Source.DISK, isExpired)
                }
            }
            else null
        } catch (e : IOException) {
            Timber.e("GIT-REST: ${e::class.simpleName} - ${e.message}")
            null
        }
    }

    @OptIn(InternalSerializationApi::class)
    private fun encodeToString(value: Any): String {
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(StoreFrontDto::class.java)
        val serializer: KSerializer<Any>
        val typeName: String
        if (value is List<*>) {
            // GiteaUser is just a default list hack for when the list is empty; it's never actually serialized in this case
            val type = value.firstOrNull()?.let { it::class } ?: DiskCache::class
            serializer = ListSerializer(type.serializer()) as KSerializer<Any>
            typeName = "list:${type.java.name}"
        } else {
            serializer = value::class.serializerOrNull() as KSerializer<Any>
            typeName = value.javaClass.name
        }

        // add type + expiration metadata before serializing
        val string = typeName + "#" + System.currentTimeMillis() + "#" + jsonConfig.encodeToString(serializer, value)
        return string
    }

    @OptIn(InternalSerializationApi::class)
    private fun <T : Any> decodeFromString(className: String, json: String): T? {
        // obtain the correct serializer for {className}
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter<T>(javaClass)

        // parse JSON if before expiry date; else return null for default behavior (fetch the actual request)
        return (adapter.fromJson(json) as? T)
    }

    override suspend fun <T : Any> getEntry(key: String, timeLimit: Duration, useEntryEvenIfExpired: Boolean, defaultValue: suspend () -> T): Entry<T> {
        val entry = getEntry<T>(key, timeLimit, useEntryEvenIfExpired)
        return entry ?: Entry(defaultValue(), System.currentTimeMillis(), Source.ORIGIN).also {
            set(key, it.data)
        }
    }

    companion object {
        inline fun <reified T> String.decodeFromString( jsonConfig: Json): T? =
            jsonConfig.decodeFromString(serializer(), this)
    }
}