package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.caldeirasoft.outcast.db.Episode

data class EpisodeViewState(
    val storeEpisode: Async<Episode> = Uninitialized
) : MavericksState
{
    constructor(episode: Episode) :
            this(storeEpisode = Success(episode))
}