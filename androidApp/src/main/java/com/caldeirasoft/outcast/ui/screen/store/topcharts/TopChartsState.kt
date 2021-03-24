package com.caldeirasoft.outcast.ui.screen.store.topcharts

import com.airbnb.mvrx.MavericksState
import com.caldeirasoft.outcast.domain.enum.StoreItemType

data class TopChartsState(
    val selectedChartTab: StoreItemType,
) : MavericksState


