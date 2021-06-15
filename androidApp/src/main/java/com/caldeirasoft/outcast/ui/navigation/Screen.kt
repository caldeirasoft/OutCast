package com.caldeirasoft.outcast.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.twotone.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.Genre
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreData.Companion.toStoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.store.storedata.Routes
import com.caldeirasoft.outcast.ui.screen.store.storedata.RoutesActions
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

enum class ScreenName {
    INBOX,
    LIBRARY,
    STORE_DATA,
    STORE_SEARCH,
    QUEUE,
    PODCAST,
    EPISODE,
    SAVED_EPISODES,
    PLAYED_EPISODES,
    MORE,
    PROFILE,
    FAVORITES,
    FILES,
    SETTINGS,
    PODCAST_SETTINGS,
    STATISTICS,
}

sealed class Screen (val id: ScreenName) {
    object Queue : Screen(ScreenName.QUEUE)
    object Profile : Screen(ScreenName.PROFILE)
    data class PodcastScreen(val feedUrl: String, val storePodcast: StorePodcast? = null) : Screen(ScreenName.PODCAST) {
        constructor(podcast: Podcast) : this(
            feedUrl = podcast.feedUrl,
        )
        constructor(storePodcast: StorePodcast) : this(
            feedUrl = storePodcast.feedUrl,
            storePodcast = storePodcast,
        )
    }

    data class PodcastSettings(val feedUrl: String) : Screen(ScreenName.PODCAST_SETTINGS)
    data class EpisodeScreen constructor(
        val feedUrl: String,
        val guid: String,
        val fromSamePodcast: Boolean = false,
        val storeEpisode: StoreEpisode? = null
    ) : Screen(ScreenName.EPISODE) {
        constructor(episode: Episode) : this(
            feedUrl = episode.feedUrl,
            guid = episode.guid
        )

        constructor(storeEpisode: StoreEpisode) : this(
            feedUrl = storeEpisode.feedUrl,
            guid = storeEpisode.guid,
            storeEpisode = storeEpisode
        )
    }

    object SavedEpisodes : Screen(ScreenName.SAVED_EPISODES)
    object PlayedEpisodes : Screen(ScreenName.PLAYED_EPISODES)

    object Settings : Screen(ScreenName.SETTINGS)
    object Statistics : Screen(ScreenName.STATISTICS)
    data class StoreDataScreen(val storeData: StoreData? = null) : Screen(ScreenName.STORE_DATA) {
        constructor(genre: Genre) : this(storeData = genre.toStoreData())
        constructor(category: Category) : this(storeData = category.toStoreData())
    }


    companion object {

    }
}
