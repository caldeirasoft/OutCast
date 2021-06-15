package com.caldeirasoft.outcast.ui.util

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavArgs
import androidx.navigation.NavArgsLazy
import androidx.navigation.NavBackStackEntry
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import java.net.URLDecoder

inline fun <reified Args : NavArgs> navArgs(savedStateHandle: SavedStateHandle) =
    NavArgsLazy(Args::class) { savedStateHandle.toBundle() }

/** Convert `SavedStateHandle` to `Bundle` */
fun SavedStateHandle.toBundle() =
    bundleOf(*this.keys().map { Pair(it, this.get<Any?>(it)) }.toTypedArray())

inline fun <reified T> NavBackStackEntry.getObjectNotNull(key: String): T {
    val chartsEncoded = arguments?.getString(key)
    return requireNotNull(Json.decodeFromString(serializer(), URLDecoder.decode(chartsEncoded, "UTF-8")))
}

inline fun <reified T> NavBackStackEntry.getObject(key: String): T? =
    arguments?.getString(key)?.let {
        Json.decodeFromString(serializer(), URLDecoder.decode(it, "UTF-8"))
    }

inline fun <reified T> SavedStateHandle.getObject(key: String): T? =
    (this@getObject.get(key) as String?)?.let {
        Json.decodeFromString(serializer(), URLDecoder.decode(it, "UTF-8"))
    }

inline fun <reified T> String.unserialize(): T =
    Json.decodeFromString(serializer(), URLDecoder.decode(this, "UTF-8"))

inline fun <reified T> SavedStateHandle.getObjectNotNull(key: String): T =
    requireNotNull((get(key) as String?)?.let {
        Json.decodeFromString(serializer(), URLDecoder.decode(it, "UTF-8"))
    })

inline fun <reified T> Bundle.getObject(key: String): T? =
    getString(key)?.let {
        Json.decodeFromString(serializer(), it)
    }
