package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.paging.PagingData
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class StorePodcastViewState(
    val podcastId: Long,
    val podcastPageAsync: Async<PodcastPage>,
    val episodes: List<Episode> = emptyList(),
    val otherPodcasts: PagingData<StoreCollection> = PagingData.empty(),
    val showAllEpisodes: Boolean = false,
    val followingStatus: FollowStatus = FollowStatus.UNFOLLOWED,
) : MavericksState {
    constructor(arg: StorePodcastArg) :
            this(podcastId = arg.id, podcastPageAsync = Loading(arg.toStorePodcast().page))

}