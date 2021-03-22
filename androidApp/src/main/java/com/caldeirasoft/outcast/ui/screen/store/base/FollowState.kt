package com.caldeirasoft.outcast.ui.screen.store.base

import com.airbnb.mvrx.MavericksState

interface FollowState : MavericksState {
    val followingStatus: Map<Long, FollowStatus>
}

