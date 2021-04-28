package com.caldeirasoft.outcast.ui.screen.store.storedata

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
data class StoreDataState(
    val storeData: StoreData? = null,
    val title: String = "",
    val storePage: StorePage? = storeData?.getPage(),
    val followingStatus: List<Long> = emptyList(),
    val followLoadingStatus: List<Long> = emptyList(),
    val newVersionAvailable: Boolean = false,
)
