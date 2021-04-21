package com.caldeirasoft.outcast.ui.screen.inbox

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.data.db.entities.Episode

data class InboxViewState(
    val episodes: List<Episode> = emptyList(),
) : MavericksState