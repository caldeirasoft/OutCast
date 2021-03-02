package com.caldeirasoft.outcast.data.util.local

import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import kotlin.time.Duration

/**
 * Naive caching implementation for the JVM that uses WeakReference
 * as a cache "buffer" that can be cleaned up by the GC when necessary.
 */
class MemoryCache(
    val underlyingCache: Cache,
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

    override suspend fun <T : Any> getEntry(key: String, timeLimit: Duration, useEntryEvenIfExpired: Boolean): Entry<T>? {
        val weakEntry = map[key]

        return weakEntry?.let {
            val expired = hasExpired(weakEntry.timestamp, timeLimit)
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

                underlyingCache.getEntry<T>(key, timeLimit, useEntryEvenIfExpired)?.let {
                    map[key] = WeakEntry(key, it as Any, it.timestamp, referenceQueue)
                    it
                }
            }
        }
    }
}