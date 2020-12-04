package com.caldeirasoft.outcast.ui.screen.store.topcharts

data class TopChartsViewState(
    val topChartsTab: List<TopChartsTab> = TopChartsTab.values().asList(),
    val selectedTopChartsTab: TopChartsTab = TopChartsTab.PODCASTS
)