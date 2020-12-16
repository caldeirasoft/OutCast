package com.caldeirasoft.outcast.ui.navigation

import androidx.navigation.NavBackStackEntry
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
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

    object Podcast : Route("podcast/{podcastId}") {
        fun buildRoute(podcastId: Long): String = "podcast/${podcastId}"
        fun getPodcastId(entry: NavBackStackEntry): Long =
            entry.arguments!!.getLong("podcastId")
                ?: throw IllegalArgumentException("podcastId argument missing.")
    }

    object StoreDirectory : Route("storeDirectory")

    object StoreGenrePage : Route("storeGenre/{genre}") {
        fun buildRoute(genre: StoreGenre) :String {
            val roomEncoded = URLEncoder.encode(Json.encodeToString(genre), "UTF-8")
            return "storeGenre/$roomEncoded"
        }

        fun getGenre(entry: NavBackStackEntry): StoreGenre {
            val genreEncoded = entry.arguments!!.getString("genre")
                ?: throw IllegalArgumentException("genre argument missing.")
            return Json.decodeFromString(StoreGenre.serializer(), URLDecoder.decode(genreEncoded, "UTF-8"))
        }
    }

    object StoreRoomPage : Route("storeRoom/{room}") {
        fun buildRoute(room: StoreRoom) :String {
            val roomEncoded = URLEncoder.encode(Json.encodeToString(room), "UTF-8")
            return "storeRoom/$roomEncoded"
        }

        fun getRoom(entry: NavBackStackEntry): StoreRoom {
            val roomEncoded = entry.arguments!!.getString("room")
                ?: throw IllegalArgumentException("room argument missing.")
            return Json.decodeFromString(StoreRoom.serializer(), URLDecoder.decode(roomEncoded, "UTF-8"))
        }
    }

    object StorePodcast : Route("storePodcast/{url}") {
        fun buildRoute(url: String): String {
            val urlEncoded = URLEncoder.encode(url, "UTF-8")
            return "storePodcast/${urlEncoded}"
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
    const val Genre = "genre"
    const val StoreFront = "storeFront"
}
