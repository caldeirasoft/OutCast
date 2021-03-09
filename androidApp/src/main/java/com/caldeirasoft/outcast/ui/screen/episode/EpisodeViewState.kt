package com.caldeirasoft.outcast.ui.screen.episode

import com.airbnb.mvrx.Async
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.Uninitialized
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode

data class EpisodeViewState(
    val storeEpisode: Async<StoreEpisode> = Uninitialized
) : MavericksState
{
    constructor(episode: StoreEpisode) :
            this(storeEpisode = Success(episode))
}