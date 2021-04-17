package com.caldeirasoft.outcast.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastArg
import com.caldeirasoft.outcast.ui.screen.store.discover.StoreDataArg
import com.caldeirasoft.outcast.ui.screen.store.discover.StoreDataArg.Companion.toStoreDataArg
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastArg
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastArg.Companion.toStorePodcastArg
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
    DISCOVER,
    STORE_SEARCH,
    STORE_CHARTS,
    STORE_PODCAST,
    STORE_EPISODES,
    MORE,
    FAVORITES,
    HISTORY,
    FILES,
    SETTINGS,
    PODCAST_SETTINGS,
    STATISTICS,
}

sealed class Screen (val id: ScreenName) {
    object Queue : Screen(ScreenName.QUEUE)
    object Inbox : Screen(ScreenName.INBOX)
    object Library : Screen(ScreenName.LIBRARY)
    object Profile : Screen(ScreenName.PROFILE)
    data class PodcastScreen(val podcastArg: PodcastArg) : Screen(ScreenName.PODCAST)
    data class PodcastSettings(val podcastId: Long) : Screen(ScreenName.PODCAST_SETTINGS)
    data class EpisodeScreen(val episodeArg: EpisodeArg) : Screen(ScreenName.EPISODE)
    object Settings : Screen(ScreenName.SETTINGS)
    object Statistics : Screen(ScreenName.STATISTICS)
    data class Discover(val storeDataArg: StoreDataArg?) : Screen(ScreenName.DISCOVER) {
        constructor(storeData: StoreData) : this(storeDataArg = storeData.toStoreDataArg())
        constructor(genre: Genre) : this(storeDataArg = genre.toStoreDataArg())
        constructor(category: Category) : this(storeDataArg = category.toStoreDataArg())
    }

    object StoreSearch : Screen(ScreenName.STORE_SEARCH)
    data class StorePodcastScreen(val podcastArg: StorePodcastArg) :
        Screen(ScreenName.STORE_PODCAST) {
        constructor(storePodcast: StorePodcast) : this(podcastArg = storePodcast.toStorePodcastArg())
        constructor(podcast: Podcast) : this(podcastArg = podcast.toStorePodcastArg())
    }

    data class Charts(val itemType: StoreItemType) : Screen(ScreenName.STORE_CHARTS)

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

    object Discover : BottomNavigationScreen(ScreenName.DISCOVER,
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

