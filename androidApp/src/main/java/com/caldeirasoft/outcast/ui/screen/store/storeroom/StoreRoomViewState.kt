package com.caldeirasoft.outcast.ui.screen.store.storeroom

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class StoreRoomViewState(
    val room: StoreRoom,
    val title: String,
    val storePage: StoreFeaturedPage = room.getPage(),
) : MavericksState {
    constructor(storeRoom: StoreRoom) :
            this(room = storeRoom,
                title = storeRoom.label)
}