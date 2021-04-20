package com.caldeirasoft.outcast.ui.screen.library

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.data.db.entities.Podcast

data class LibraryViewState(
    val podcasts: List<Podcast> = emptyList(),
) : MavericksState {
}