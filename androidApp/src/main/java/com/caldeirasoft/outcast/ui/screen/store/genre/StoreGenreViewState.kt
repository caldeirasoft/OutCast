package com.caldeirasoft.outcast.ui.screen.store.genre

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.models.Genre

data class StoreGenreViewState(
    val genreId: Int,
    val title: String,
) : MavericksState {
    constructor(genre: Genre) :
            this(genreId = genre.id,
                title = genre.name)
}