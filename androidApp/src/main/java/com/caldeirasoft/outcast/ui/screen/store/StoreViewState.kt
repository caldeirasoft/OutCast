package com.caldeirasoft.outcast.ui.screen.store

import com.caldeirasoft.outcast.ui.util.ScreenState

data class StoreViewState(
    val storeTabs: List<StoreTab> = StoreTab.values().asList(),
    val selectedStoreTab: StoreTab = StoreTab.DISCOVER,
    val topChartsTab: List<TopChartsTab> = TopChartsTab.values().asList(),
    val selectedTopChartsTab: TopChartsTab = TopChartsTab.PODCASTS,
    val screenState: ScreenState = ScreenState.Idle
)