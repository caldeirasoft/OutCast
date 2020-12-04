package com.caldeirasoft.outcast.ui.screen.store

data class StoreViewState(
    val storeTabs: List<StoreTab> = StoreTab.values().asList(),
    val selectedStoreTab: StoreTab = StoreTab.DISCOVER
)