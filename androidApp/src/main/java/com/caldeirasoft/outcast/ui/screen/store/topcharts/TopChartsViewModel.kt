package com.caldeirasoft.outcast.ui.screen.storedirectory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsTab
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartsViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalCoroutinesApi
class TopChartsViewModel : ViewModel() {

    private val selectedTopChartsTab = MutableStateFlow(TopChartsTab.PODCASTS)
    private val topChartsTabs = MutableStateFlow(TopChartsTab.values().asList())

    val state: StateFlow<TopChartsViewState> =
        combine(
            topChartsTabs,
            selectedTopChartsTab
        ) { tabs, selectedTab -> TopChartsViewState(tabs, selectedTab)
        }.stateIn(viewModelScope, SharingStarted.Lazily, TopChartsViewState())

    fun onTopChartsTabSelected(topChartsTab: TopChartsTab) {
        selectedTopChartsTab.value = topChartsTab
    }
}