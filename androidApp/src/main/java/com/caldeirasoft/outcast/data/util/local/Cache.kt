package com.caldeirasoft.outcast.data.util.local

import kotlin.time.Duration
import kotlin.time.days
import kotlin.time.milliseconds

interface Cache {
    suspend fun set(key: String, value: Any)
    suspend fun <T : Any> getEntry(id: String, timeLimit: Duration = DEFAULT_DURATION, useEntryEvenIfExpired: Boolean = false) : Entry<T>?

    suspend fun <T : Any> get(key: String, timeLimit: Duration = DEFAULT_DURATION): T? {
        val entry = getEntry<T>(key)
        return entry?.data
    }

    suspend fun <T : Any> get(key: String, timeLimit: Duration = DEFAULT_DURATION, useEntryEvenIfExpired: Boolean = false, defaultValue: suspend () -> T): T? {
        val entry = getEntry<T>(key, timeLimit, useEntryEvenIfExpired, defaultValue)
        return entry.data
    }

    /*
    suspend fun <T : Any> getWithSource(key: String, useEntryEvenIfExpired: Boolean = false): Pair<T, Source>? {
        val entry = getEntry<T>(key)
        return entry?.let { Pair(entry.data, entry.source) }
    }

    suspend fun <T : Any> getWithSource(key: String, useEntryEvenIfExpired: Boolean = false, defaultValue: suspend () -> T): Pair<T, Source> {
        val entry = getEntry<T>(key, useEntryEvenIfExpired, defaultValue)
        return Pair(entry.data, entry.source)
    }
     */

    suspend fun <T : Any> getEntry(key: String, timeLimit: Duration = DEFAULT_DURATION, useEntryEvenIfExpired: Boolean = false, defaultValue: suspend () -> T): Entry<T> {
        val entry = getEntry<T>(key)
        return entry ?: Entry(defaultValue(), System.currentTimeMillis(), Source.ORIGIN).also {
            set(key, it.data)
        }
    }

    fun hasExpired(persistedTimestamp: Long, timeLimit: Duration): Boolean {
        val now = System.currentTimeMillis()
        val durationSincePersisted = (now - persistedTimestamp).milliseconds
        return durationSincePersisted > timeLimit
    }

    companion object {
        val DEFAULT_DURATION = 10.days
    }
}