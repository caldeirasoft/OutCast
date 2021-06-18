package com.caldeirasoft.outcast.ui.util

import android.os.Bundle
import androidx.navigation.NavController
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreData.Companion.toStoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.store.storedata.RoutesActions
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

inline fun <reified T> encodeObject(item: T): String =
    Json.encodeToString(item)

inline fun <reified T> jsonUrlEncodeObject(item: T): String =
    URLEncoder.encode(Json.encodeToString(item), "UTF-8")

fun String.urlEncode(): String =
    URLEncoder.encode(this, "UTF-8")

fun String.urlDecode(): String =
    URLDecoder.decode(this, "UTF-8")

fun NavController.navigateToPodcast(feedUrl: String) {
    val feedUrlEncode = feedUrl.urlEncode()
    this.navigate(RoutesActions.toPodcast(feedUrlEncode))
}

fun NavController.navigateToPodcast(podcast: Podcast) {
    val feedUrl = podcast.feedUrl.urlEncode()
    this.navigate(RoutesActions.toPodcast(feedUrl))
}

fun NavController.navigateToPodcast(storePodcast: StorePodcast) {
    this.currentBackStackEntry?.arguments = Bundle().apply {
        putString("podcast", encodeObject(storePodcast))
    }
    val feedUrl = storePodcast.feedUrl.urlEncode()
    this.navigate(RoutesActions.toPodcast(feedUrl))
}

fun NavController.navigateToStore(storeData: StoreData) {
    val storeDataEncoded = jsonUrlEncodeObject(storeData)
    this.navigate(RoutesActions.toStore(storeDataEncoded))
}

fun NavController.navigateToStore(category: Category) {
    val storeData = category.toStoreData()
    val storeDataEncoded = jsonUrlEncodeObject(storeData)
    this.navigate(RoutesActions.toStore(storeDataEncoded))
}

fun NavController.navigateToEpisode(episode: Episode, fromSamePodcast: Boolean = false) {
    this.currentBackStackEntry?.arguments = Bundle().apply {
        putBoolean("fromSamePodcast", fromSamePodcast)
    }

    val feedUrl = episode.feedUrl.urlEncode()
    val guid = episode.guid.urlEncode()
    this.navigate(RoutesActions.toEpisode(feedUrl, guid))
}

fun NavController.navigateToEpisode(storeEpisode: StoreEpisode) {
    this.currentBackStackEntry?.arguments = Bundle().apply {
        putString("episode", encodeObject(storeEpisode))
    }

    val feedUrl = storeEpisode.feedUrl.urlEncode()
    val guid = storeEpisode.guid.urlEncode()
    this.navigate(RoutesActions.toEpisode(feedUrl, guid))
}