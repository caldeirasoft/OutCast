package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.paging.PagingData
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class StorePodcastViewState(
    val podcast: Podcast,
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val storeFront: String? = null,
    val podcastPageAsync: Async<PodcastPage> = Uninitialized,
    val episodes: PagingData<Episode> = PagingData.empty(),
    val otherPodcasts: PagingData<StoreCollection> = PagingData.empty(),
    val showAllEpisodes: Boolean = false,
    val followingStatus: FollowStatus = FollowStatus.UNFOLLOWED,
) : MavericksState {
    constructor(arg: StorePodcastArg) :
            this(podcast = arg.toPodcast(), isLoading = true)

    val artistRoom: StoreRoom?
        get() =
            storeFront?.let {
                podcast.artistUrl?.let {
                    StoreRoom(
                        id = podcast.artistId ?: 0L,
                        label = podcast.artistName,
                        url = podcast.artistUrl.orEmpty(),
                        storeFront = storeFront
                    )
                }
            }
}