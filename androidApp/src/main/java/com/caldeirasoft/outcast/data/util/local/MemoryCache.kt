package com.caldeirasoft.outcast.data.util.local

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference

/**
 * Naive caching implementation for the JVM that uses WeakReference
 * as a cache "buffer" that can be cleaned up by the GC when necessary.
 */
class MemoryCache(
    val underlyingCache: Cache,
    val cacheDuration: Long = 86400000 // cache for ~1 day by default
) : Cache {

    private class WeakEntry internal constructor(
        internal val key: String,
        value: Any,
        internal val timestamp: Long,
        referenceQueue: ReferenceQueue<Any>) : WeakReference<Any>(value, referenceQueue)

    private val referenceQueue = ReferenceQueue<Any>()
    private val map = HashMap<String, WeakEntry>()

    override suspend fun set(key: String, value: Any) {
        // remove any invalid references
        var weakEntry = referenceQueue.poll() as WeakEntry?
        while (weakEntry != null) {
            map.remove(weakEntry.key)
            weakEntry = referenceQueue.poll() as WeakEntry?
        }

        val now = System.currentTimeMillis()
        map[key] = WeakEntry(key, value, now, referenceQueue)
        underlyingCache.set(key, value)
    }

    override suspend fun <T : Any> getEntry(key: String, useEntryEvenIfExpired: Boolean): Entry<T>? {
        val weakEntry = map[key]

        return weakEntry?.let {
            val expired = hasExpired(weakEntry.timestamp, cacheDuration)
            if (expired && !useEntryEvenIfExpired) {
                // has expired -> remove entry from cache
                map.remove(key)
                //underlyingCache.remove(key)
                null
            } else {
                // get entry
                weakEntry.get()?.let {
                    Entry(it, weakEntry.timestamp, Source.MEM, expired) as Entry<T>
                }

                underlyingCache.getEntry<T>(key, useEntryEvenIfExpired)?.let {
                    map[key] = WeakEntry(key, it as Any, it.timestamp, referenceQueue)
                    it
                }
            }
        }
    }
}