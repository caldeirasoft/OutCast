package com.caldeirasoft.outcast.data.util.local

interface Cache {
    suspend fun set(key: String, value: Any)
    suspend fun <T : Any> getEntry(id: String, useEntryEvenIfExpired: Boolean = false) : Entry<T>?

    suspend fun <T : Any> get(key: String): T? {
        val entry = getEntry<T>(key)
        return entry?.data
    }

    suspend fun <T : Any> get(key: String, useEntryEvenIfExpired: Boolean = false, defaultValue: suspend () -> T): T? {
        val entry = getEntry<T>(key, useEntryEvenIfExpired, defaultValue)
        return entry.data
    }

    suspend fun <T : Any> getWithSource(key: String, useEntryEvenIfExpired: Boolean = false): Pair<T, Source>? {
        val entry = getEntry<T>(key)
        return entry?.let { Pair(entry.data, entry.source) }
    }

    suspend fun <T : Any> getWithSource(key: String, useEntryEvenIfExpired: Boolean = false, defaultValue: suspend () -> T): Pair<T, Source> {
        val entry = getEntry<T>(key, useEntryEvenIfExpired, defaultValue)
        return Pair(entry.data, entry.source)
    }

    suspend fun <T : Any> getEntry(key: String, useEntryEvenIfExpired: Boolean = false, defaultValue: suspend () -> T): Entry<T> {
        val entry = getEntry<T>(key)
        return entry ?: Entry(defaultValue(), System.currentTimeMillis(), Source.ORIGIN).also {
            set(key, it.data)
        }
    }

    fun hasExpired(persistedTimestamp: Long, timeLimit: Long): Boolean {
        val now = System.currentTimeMillis()
        val durationSincePersisted = (now - persistedTimestamp)
        return durationSincePersisted > timeLimit
    }
}