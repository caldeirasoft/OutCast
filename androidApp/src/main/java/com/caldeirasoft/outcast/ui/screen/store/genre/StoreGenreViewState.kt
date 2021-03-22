package com.caldeirasoft.outcast.ui.screen.store.genre

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.ui.screen.store.base.FollowState
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class StoreGenreViewState(
    val genreId: Int,
    val title: String,
    override val followingStatus: Map<Long, FollowStatus> = emptyMap(),
) : MavericksState, FollowState {
    constructor(genre: Genre) :
            this(genreId = genre.id,
                title = genre.name)
}