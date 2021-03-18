package com.caldeirasoft.outcast.ui.screen.podcast

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.db.Episode
import com.caldeirasoft.outcast.db.Podcast

data class PodcastViewState(
    val podcast: Podcast,
    val episodes: List<Episode> = emptyList(),
) : MavericksState {
    constructor(arg: PodcastArg) :
            this(podcast = arg.toPodcast())
}