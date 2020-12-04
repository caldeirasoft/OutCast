package com.caldeirasoft.outcast.ui.screen.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class StoreViewModel : ViewModel() {

    private val selectedStoreTab = MutableStateFlow(StoreTab.DISCOVER)
    private val storeTabs = MutableStateFlow(StoreTab.values().asList())

    val state: StateFlow<StoreViewState> =
        combine(
            storeTabs,
            selectedStoreTab
        ) { tabs, selectedTab -> StoreViewState(tabs, selectedTab)
        }.stateIn(viewModelScope, SharingStarted.Lazily, StoreViewState())

    fun onStoreTabSelected(storeTab: StoreTab) {
        selectedStoreTab.value = storeTab
    }
}


