package com.caldeirasoft.outcast.data.util.local

data class Entry<T>(val data: T, val timestamp: Long, val source: Source, val isExpired: Boolean = false)