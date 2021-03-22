package com.caldeirasoft.outcast.ui.screen.store.storeroom

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.ui.screen.store.base.FollowState
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class StoreRoomViewState(
    val room: StoreRoom,
    val title: String,
    val storePage: StoreFeaturedPage = room.getPage(),
    override val followingStatus: Map<Long, FollowStatus> = emptyMap(),
) : MavericksState, FollowState {
    constructor(storeRoom: StoreRoom) :
            this(room = storeRoom,
                title = storeRoom.label)
}