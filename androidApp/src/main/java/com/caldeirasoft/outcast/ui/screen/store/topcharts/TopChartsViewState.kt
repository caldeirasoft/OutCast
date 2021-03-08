package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.paging.PagingData
import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreGenreData

data class TopChartsViewState(
    val selectedChartTab: StoreItemType = StoreItemType.PODCAST,
    val selectedGenre: Int? = null,
    val categories: StoreGenreData? = null,
    val discover: PagingData<StoreItem> = PagingData.empty(),
) : MavericksState {
    constructor(itemType: StoreItemType) :
            this(selectedChartTab = itemType)
}


