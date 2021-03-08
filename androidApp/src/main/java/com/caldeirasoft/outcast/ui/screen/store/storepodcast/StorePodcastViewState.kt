package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.paging.PagingData
import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Uninitialized
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage

data class StorePodcastViewState(
    val storePodcast: StorePodcast,
    val storePodcastPage: Async<StorePodcastPage> = Uninitialized,
    val otherPodcasts: PagingData<StoreItem> = PagingData.empty()
) : MavericksState {
    constructor(podcast: StorePodcast) :
            this(storePodcast = podcast)
}