package com.caldeirasoft.outcast.ui.screen.store.discover

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePage
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class DiscoverState(
    val storeData: StoreData,
    val title: String = "",
    val storePage: StorePage = storeData.getPage(),
    val followingStatus: Map<String, FollowStatus> = emptyMap(),
    val newVersionAvailable: Boolean = false,
) : MavericksState {
    constructor() : this(storeData = StoreData.Default)
    constructor(storeDataArg: StoreDataArg?) :
            this(storeData = storeDataArg?.toStoreData() ?: StoreData.Default,
                title = storeDataArg?.label.orEmpty())
}