package com.caldeirasoft.outcast.ui.screen.store.discover

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePage
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class DiscoverState(
    val storeData: StoreData? = null,
    val title: String = "",
    val storePage: StorePage? = storeData?.getPage(),
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
    val newVersionAvailable: Boolean = false,
) : MavericksState {
    constructor() : this(storeData = StoreData.Default)
    constructor(storeDataArg: StoreDataArg?) :
            this(storeData = storeDataArg?.toStoreData() ?: StoreData.Default,
                title = storeDataArg?.label.orEmpty())
}

