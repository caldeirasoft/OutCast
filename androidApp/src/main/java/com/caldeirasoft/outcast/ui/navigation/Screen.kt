package com.caldeirasoft.outcast.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

enum class ScreenName {
    QUEUE,
    INBOX,
    LIBRARY,
    PROFILE,
    PODCAST,
    STORE_DISCOVER,
    STORE_SEARCH,
    STORE_CHARTS,
    STORE_GENRE,
    STORE_ROOM,
    STORE_PODCAST,
    STORE_EPISODES,
    SETTINGS,
    STATISTICS,
}

sealed class Screen (val id: ScreenName) {
    object Queue : Screen(ScreenName.QUEUE)
    object Inbox : Screen(ScreenName.INBOX)
    object Library : Screen(ScreenName.LIBRARY)
    object Profile : Screen(ScreenName.PROFILE)
    data class PodcastScreen(val podcast: StorePodcast) : Screen(ScreenName.PODCAST)
    object Settings : Screen(ScreenName.SETTINGS)
    object Statistics : Screen(ScreenName.STATISTICS)
    object StoreDiscover : Screen(ScreenName.STORE_DISCOVER)
    object StoreSearch : Screen(ScreenName.STORE_SEARCH)
    data class Charts(val itemType: StoreItemType) : Screen(ScreenName.STORE_CHARTS)
    data class Genre(val genreId: Int, val title: String) : Screen(ScreenName.STORE_GENRE)
    data class Room(val room: StoreRoom) : Screen(ScreenName.STORE_ROOM)
    data class StorePodcastScreen(val podcast: StorePodcast) : Screen(ScreenName.STORE_PODCAST)
    data class StoreEpisodesScreen(val podcast: StorePodcast) : Screen(ScreenName.STORE_EPISODES)

    companion object {
        inline fun <reified T> encodeObject(item: T): String =
            URLEncoder.encode(Json.encodeToString(item), "UTF-8")
    }
}

sealed class BottomNavigationScreen(val id: ScreenName, @StringRes val resourceId: Int, val icon: ImageVector)
{
    object Queue : BottomNavigationScreen(ScreenName.QUEUE, R.string.screen_queue, Icons.Default.QueueMusic)
    object Library : BottomNavigationScreen(ScreenName.LIBRARY, R.string.screen_queue, Icons.Default.LibraryMusic)
    object Discover : BottomNavigationScreen(ScreenName.STORE_DISCOVER, R.string.screen_queue, Icons.Default.Web)
    object Search : BottomNavigationScreen(ScreenName.STORE_SEARCH, R.string.screen_search, Icons.Default.Search)
    object Profile : BottomNavigationScreen(ScreenName.PROFILE, R.string.screen_queue, Icons.Default.Person)
}

