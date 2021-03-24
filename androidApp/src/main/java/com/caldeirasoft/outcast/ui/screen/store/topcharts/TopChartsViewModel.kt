package com.caldeirasoft.outcast.ui.screen.store.topcharts

import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent

@OptIn(KoinApiExtension::class)
@FlowPreview
@ExperimentalCoroutinesApi
class TopChartsViewModel(
    initialState: TopChartsState,
) : MavericksViewModel<TopChartsState>(initialState), KoinComponent {

    fun onTabSelected(tab: StoreItemType) {
        setState {
            copy(selectedChartTab = tab)
        }
    }
}


