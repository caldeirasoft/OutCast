package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.paging.PagingData
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.models.store.StorePodcast

data class StorePodcastViewState(
    val podcastPageAsync: Async<PodcastPage>,
    val episodes: List<Episode> = emptyList(),
    val otherPodcasts: PagingData<StoreCollection> = PagingData.empty(),
    val showAllEpisodes: Boolean = false,
) : MavericksState {
    constructor(arg: StorePodcast) :
            this(podcastPageAsync = Loading(arg.page))

}