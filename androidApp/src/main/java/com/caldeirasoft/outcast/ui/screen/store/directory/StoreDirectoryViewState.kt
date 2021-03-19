package com.caldeirasoft.outcast.ui.screen.store.directory

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.models.store.StoreGroupingPage

data class StoreDirectoryViewState(
    val storeData: StoreGroupingPage? = null,
    val storeFront: String? = null,
) : MavericksState
