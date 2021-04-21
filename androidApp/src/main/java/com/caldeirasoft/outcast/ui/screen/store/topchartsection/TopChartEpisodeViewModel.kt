package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import com.airbnb.mvrx.MavericksViewModelFactory
import com.caldeirasoft.outcast.data.db.dao.PodcastDao
import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.hiltMavericksViewModelFactory
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadFollowedPodcastsUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadStoreTopChartsPagingDataUseCase
import com.caldeirasoft.outcast.domain.usecase.FollowUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class TopChartEpisodeViewModel @AssistedInject constructor(
    @Assisted initialState: TopChartSectionState,
    followUseCase: FollowUseCase,
    loadFollowedPodcastsUseCase: LoadFollowedPodcastsUseCase,
    loadStoreTopChartsPagingDataUseCase: LoadStoreTopChartsPagingDataUseCase,
    fetchStoreFrontUseCase: FetchStoreFrontUseCase,
    podcastDao: PodcastDao
) : TopChartSectionViewModel(
    initialState,
    StoreItemType.EPISODE,
    followUseCase,
    loadFollowedPodcastsUseCase,
    loadStoreTopChartsPagingDataUseCase,
    fetchStoreFrontUseCase,
    podcastDao
) {
    @AssistedFactory
    interface Factory : AssistedViewModelFactory<TopChartEpisodeViewModel, TopChartSectionState> {
        override fun create(initialState: TopChartSectionState): TopChartEpisodeViewModel
    }

    companion object :
        MavericksViewModelFactory<TopChartEpisodeViewModel, TopChartSectionState> by hiltMavericksViewModelFactory()
}

