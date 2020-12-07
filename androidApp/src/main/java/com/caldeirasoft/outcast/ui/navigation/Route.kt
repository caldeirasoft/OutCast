package com.caldeirasoft.outcast.ui.navigation

import androidx.navigation.NavBackStackEntry
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Route(val name: String) {

    object Inbox : Route("inbox")

    object Podcasts : Route("podcasts")

    object StoreDirectory : Route("storeDirectory")

    object Podcast : Route("podcast") {
        fun buildRoute(podcastId: Long): String = "$name/${podcastId}"
        fun getPodcastId(entry: NavBackStackEntry): Long =
            entry.arguments!!.getLong("podcastId")?.toLong()
                ?: throw IllegalArgumentException("podcastId argument missing.")
    }

    object StoreCollectionPage : Route("storeCollection") {
        fun buildRoute(room: StoreRoom) :String {
            val roomEncoded = URLEncoder.encode(Json.encodeToString(room), "UTF-8")
            return "$name/$roomEncoded"
        }

        fun getRoom(entry: NavBackStackEntry): StoreRoom {
            val roomEncoded = entry.arguments!!.getString("room")
                ?: throw IllegalArgumentException("room argument missing.")
            return Json.decodeFromString(StoreRoom.serializer(), URLDecoder.decode(roomEncoded, "UTF-8"))
        }
    }

    object StorePodcast : Route("storePodcast") {
        fun buildRoute(url: String): String {
            val urlEncoded = URLEncoder.encode(url, "UTF-8")
            return "$name/${urlEncoded}"
        }

        fun getUrl(entry: NavBackStackEntry): String {
            return entry.arguments!!.getString("url")
                ?: throw IllegalArgumentException("url argument missing.")
        }
    }
}

object NavArgs {
    const val PodcastId = "podcastId"
    const val Url = "url"
    const val Room = "room"
    const val StoreFront = "storeFront"
}
