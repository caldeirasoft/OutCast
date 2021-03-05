package com.caldeirasoft.outcast.ui.screen.store.directory

import androidx.paging.PagingData
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage

data class StoreDirectoryViewState(
    val storeData: StoreGroupingPage? = null,
    val storeFront: String? = null,
    val discover: PagingData<StoreItem> = PagingData.empty()
) : MavericksState
