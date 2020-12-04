package com.caldeirasoft.outcast.ui.screen.store.topcharts

enum class TopChartsTab {
    PODCASTS,
    EPISODES,
}

inline fun TopChartsTab.isPodcast() =
    this == TopChartsTab.PODCASTS

inline fun TopChartsTab.isEpisode() =
    this == TopChartsTab.EPISODES