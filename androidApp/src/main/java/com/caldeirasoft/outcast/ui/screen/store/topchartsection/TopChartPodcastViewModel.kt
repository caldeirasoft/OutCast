package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.SubscribeUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TopChartPodcastViewModel @AssistedInject constructor(
    @Assisted initialState: TopChartSectionState,
    followUseCase: SubscribeUseCase,
    loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase,
    loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
) : TopChartSectionViewModel(
    initialState,
    StoreItemType.PODCAST,
    followUseCase,
    loadFollowedPodcastsUseCase,
    loadStoreTopChartsPagingDataUseCase,
    fetchStoreFrontUseCase
) {
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<TopChartPodcastViewModel, TopChartSectionState> {
        override fun create(initialState: TopChartSectionState): TopChartPodcastViewModel
    }

    companion object :
        MavericksViewModelFactory<TopChartPodcastViewModel, TopChartSectionState> by hiltMavericksViewModelFactory()
}

