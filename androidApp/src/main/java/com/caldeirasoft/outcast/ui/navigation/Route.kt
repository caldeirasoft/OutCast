package com.caldeirasoft.outcast.ui.navigation

import androidx.navigation.NavBackStackEntry
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Class defining the screens we have in the app: home, article details and interests
 */
sealed class Route(val name: String) {

    object Inbox : Route("inbox")

    object Podcasts : Route("podcasts")

    object Discover : Route("discover")

    object Podcast : Route("podcast") {
        fun buildRoute(podcastId: Long): String = "podcast/${podcastId}"
        fun getPodcastId(entry: NavBackStackEntry): Long =
            entry.arguments!!.getString("podcastId")?.toLong()
                ?: throw IllegalArgumentException("podcastId argument missing.")
    }

    object StoreEntry : Route("storeEntry") {
        fun buildRoute(url: String): String {
            val urlEncoded = URLEncoder.encode(url, "UTF-8")
            return "storeEntry/${urlEncoded}"
        }
        fun getUrl(entry: NavBackStackEntry): String {
            val urlEncoded = entry.arguments!!.getString("url")
                ?: throw IllegalArgumentException("url argument missing.")
            val url = URLDecoder.decode(urlEncoded, "UTF-8")
            return url
        }

        enum class Argument(val key: String) {
            Url("Url")
        }
    }
}

object NavigationArguments {
    const val Url = "url"
}
