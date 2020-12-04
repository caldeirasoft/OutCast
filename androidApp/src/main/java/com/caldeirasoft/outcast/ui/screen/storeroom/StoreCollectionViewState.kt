package com.caldeirasoft.outcast.ui.screen.storeroom

import com.caldeirasoft.outcast.domain.interfaces.StoreData
import com.caldeirasoft.outcast.ui.util.ScreenState

data class StoreCollectionViewState(
    val screenState: ScreenState = ScreenState.Idle,
    val storeData: StoreData? = null
)