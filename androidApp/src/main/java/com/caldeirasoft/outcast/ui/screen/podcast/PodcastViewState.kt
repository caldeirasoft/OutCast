package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.paging.PagingData
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.db.EpisodeSummary
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.interfaces.StoreCollection

data class PodcastViewState(
    val podcast: Podcast,
    val episodes: PagingData<EpisodeSummary> = PagingData.empty(),
    val trailerEpisode: EpisodeSummary? = null,
    val otherPodcasts: PagingData<StoreCollection> = PagingData.empty()
) : MavericksState {
    constructor(arg: Podcast) :
            this(podcast = arg)
}