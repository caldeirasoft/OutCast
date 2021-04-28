package com.caldeirasoft.outcast.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.data.db.entities.Podcast.Companion.toPodcast
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.podcast
import com.caldeirasoft.outcast.domain.models.store.Genre
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreData.Companion.toStoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
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
    EPISODE_STORE,
    STORE_DATA,
    STORE_SEARCH,
    STORE_CHARTS,
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
    data class PodcastScreen constructor(val podcast: Podcast) : Screen(ScreenName.PODCAST) {
        constructor(storePodcast: StorePodcast) : this(podcast = storePodcast.toPodcast())
    }

    data class PodcastSettings(val podcastId: Long) : Screen(ScreenName.PODCAST_SETTINGS)
    data class EpisodeScreen constructor(
        val episode: Episode,
        val fromSamePodcast: Boolean = false,
    ) : Screen(ScreenName.EPISODE)

    class EpisodeStoreScreen private constructor(val episode: Episode, val podcast: Podcast) : Screen(ScreenName.EPISODE_STORE) {
        constructor(storeEpisode: StoreEpisode) : this(episode = storeEpisode.episode, podcast = storeEpisode.storePodcast.podcast)
    }

    object Settings : Screen(ScreenName.SETTINGS)
    object Statistics : Screen(ScreenName.STATISTICS)
    data class StoreDataScreen(val storeData: StoreData? = null) : Screen(ScreenName.STORE_DATA) {
        constructor(genre: Genre) : this(storeData = genre.toStoreData())
        constructor(category: Category) : this(storeData = category.toStoreData())
    }

    object StoreSearch : Screen(ScreenName.STORE_SEARCH)

    data class Charts(val itemType: StoreItemType) : Screen(ScreenName.STORE_CHARTS)

    companion object {
        inline fun <reified T> encodeObject(item: T): String =
            URLEncoder.encode(Json.encodeToString(item), "UTF-8")

        fun String.urlEncode(): String =
            URLEncoder.encode(this, "UTF-8")
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

    object Discover : BottomNavigationScreen(ScreenName.STORE_DATA,
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

