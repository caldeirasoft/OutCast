package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class TopChartSectionState(
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
) : MavericksState


