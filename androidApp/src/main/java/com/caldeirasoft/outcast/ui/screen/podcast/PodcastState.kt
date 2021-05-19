package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.datastore.preferences.core.Preferences
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.enums.PodcastFilter
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class PodcastState(
    val feedUrl: String,
    val podcast: Podcast? = null,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val storeFront: String? = null,
    val episodes: List<Episode> = emptyList(),
    val showAllEpisodes: Boolean = false,
    val followingStatus: FollowStatus = FollowStatus.UNFOLLOWED,
    val prefs: Preferences? = null,
    val sortOrder: SortOrder? = null,
    val filter: PodcastFilter = PodcastFilter.ALL
) {
    val artistData: StoreData? =
        podcast?.artistUrl?.let {
            StoreData(
                id = podcast.artistId ?: 0L,
                label = podcast.artistName,
                url = podcast.artistUrl.orEmpty(),
                storeFront = ""
            )
        }
}