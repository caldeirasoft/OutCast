package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.paging.PagingData
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.interfaces.StoreFeaturedPage
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class StoreRoomViewState(
    val room: StoreRoom,
    val title: String,
    val storePage: StoreFeaturedPage = room.getPage(),
    val discover: PagingData<StoreItem> = PagingData.empty(),
) : MavericksState {
    constructor(storeRoom: StoreRoom) :
            this(room = storeRoom,
                title = storeRoom.label)
}