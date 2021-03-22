package com.caldeirasoft.outcast.ui.screen.store.topcharts

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreGenreData
import com.caldeirasoft.outcast.ui.screen.store.base.FollowState
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus

data class TopChartsViewState(
    val selectedChartTab: StoreItemType = StoreItemType.PODCAST,
    val selectedGenre: Int? = null,
    val categories: StoreGenreData? = null,
    override val followingStatus: Map<Long, FollowStatus> = emptyMap(),
) : MavericksState, FollowState {
    constructor(itemType: StoreItemType) :
            this(selectedChartTab = itemType)
}


