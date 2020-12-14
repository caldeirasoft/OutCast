package com.caldeirasoft.outcast.ui.screen.store

import com.caldeirasoft.outcast.domain.interfaces.StoreCollectionPodcastsEpisodes
import com.caldeirasoft.outcast.domain.models.Genre
import com.caldeirasoft.outcast.domain.models.store.StoreChart
import com.caldeirasoft.outcast.domain.models.store.StoreDirectory
import com.caldeirasoft.outcast.domain.models.store.StoreGenre
import com.caldeirasoft.outcast.domain.models.store.StoreGenreMapData
import com.caldeirasoft.outcast.ui.util.ScreenState

data class StoreViewState(
    val storeTabs: List<StoreTab> = StoreTab.values().asList(),
    val selectedStoreTab: StoreTab = StoreTab.DISCOVER,
    val chartList: List<Chart> = Chart.values().asList(),
    val selectedChart: Chart = Chart.PODCASTS,
    val screenState: ScreenState = ScreenState.Idle,
)