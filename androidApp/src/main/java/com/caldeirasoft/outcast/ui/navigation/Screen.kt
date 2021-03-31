package com.caldeirasoft.outcast.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastArg
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastArg
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder

enum class ScreenName {
    QUEUE,
    INBOX,
    LIBRARY,
    PROFILE,
    PODCAST,
    EPISODE,
    STORE_DISCOVER,
    STORE_SEARCH,
    STORE_CHARTS,
    STORE_GENRE,
    STORE_ROOM,
    STORE_CATEGORIES,
    STORE_PODCAST,
    STORE_EPISODES,
    MORE,
    FAVORITES,
    HISTORY,
    FILES,
    SETTINGS,
    STATISTICS,
}

sealed class Screen (val id: ScreenName) {
    object Queue : Screen(ScreenName.QUEUE)
    object Inbox : Screen(ScreenName.INBOX)
    object Library : Screen(ScreenName.LIBRARY)
    object Profile : Screen(ScreenName.PROFILE)
    data class PodcastScreen(val podcastArg: PodcastArg) : Screen(ScreenName.PODCAST)
    data class EpisodeScreen(val episodeArg: EpisodeArg) : Screen(ScreenName.EPISODE)
    object Settings : Screen(ScreenName.SETTINGS)
    object Statistics : Screen(ScreenName.STATISTICS)
    object StoreDiscover : Screen(ScreenName.STORE_DISCOVER)
    object StoreSearch : Screen(ScreenName.STORE_SEARCH)
    data class StorePodcastScreen(val podcast: StorePodcastArg) : Screen(ScreenName.STORE_PODCAST)
    data class Charts(val itemType: StoreItemType) : Screen(ScreenName.STORE_CHARTS)
    data class GenreScreen(val genre: Genre) : Screen(ScreenName.STORE_GENRE)
    data class Room(val room: StoreRoom) : Screen(ScreenName.STORE_ROOM)
    data class StoreEpisodesScreen(val podcast: StorePodcast) : Screen(ScreenName.STORE_EPISODES)

    companion object {
        inline fun <reified T> encodeObject(item: T): String =
            URLEncoder.encode(Json.encodeToString(item), "UTF-8")
    }
}

sealed class BottomNavigationScreen(
    val id: ScreenName,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
) {
    object Inbox : BottomNavigationScreen(ScreenName.INBOX,
        R.string.screen_inbox,
        Icons.Outlined.Inbox,
        Icons.Filled.Inbox)

    object Library : BottomNavigationScreen(ScreenName.LIBRARY,
        R.string.screen_library,
        Icons.Outlined.Subscriptions,
        Icons.Filled.Subscriptions)

    object Discover : BottomNavigationScreen(ScreenName.STORE_DISCOVER,
        R.string.screen_discover,
        Icons.Default.Explore,
        Icons.Filled.Explore
    )

    object Search : BottomNavigationScreen(ScreenName.STORE_SEARCH,
        R.string.screen_search,
        Icons.Outlined.Search,
        Icons.Filled.Search)

    object More :
        BottomNavigationScreen(ScreenName.MORE, R.string.screen_more, Icons.Default.MoreHoriz)
}

