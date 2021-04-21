package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.datastore.preferences.core.Preferences
import androidx.paging.PagingData
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.data.common.PodcastPreferenceKeys
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class PodcastState(
    val podcast: Podcast,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val storeFront: String? = null,
    val episodes: List<Episode> = emptyList(),
    val showAllEpisodes: Boolean = false,
    val followingStatus: FollowStatus = FollowStatus.UNFOLLOWED,
    val prefs: Preferences? = null,
) : MavericksState {
    constructor(arg: PodcastArg) :
            this(podcast = arg.toPodcast(), isLoading = true)

    val artistData: StoreData? =
        storeFront?.let {
            podcast.artistUrl?.let {
                StoreData(
                    id = podcast.artistId ?: 0L,
                    label = podcast.artistName,
                    url = podcast.artistUrl.orEmpty(),
                    storeFront = storeFront
                )
            }
        }

    val podcastPreferenceKeys: PodcastPreferenceKeys =
        PodcastPreferenceKeys(podcast.feedUrl)
}