package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.MavericksViewModelComponent
import com.caldeirasoft.outcast.di.hiltmavericks.ViewModelKey
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastViewModel
import com.caldeirasoft.outcast.ui.screen.store.discover.DiscoverViewModel
import com.caldeirasoft.outcast.ui.screen.store.topchartsection.TopChartSectionViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelsModule {
    @[Binds IntoMap ViewModelKey(DiscoverViewModel::class)]
    fun DiscoverViewModelFactory(factory: DiscoverViewModel.Factory): AssistedViewModelFactory<*, *>

    @[Binds IntoMap ViewModelKey(PodcastViewModel::class)]
    fun PodcastViewModelFactory(factory: PodcastViewModel.Factory): AssistedViewModelFactory<*, *>

    @[Binds IntoMap ViewModelKey(TopChartSectionViewModel::class)]
    fun TopChartSectionViewModelFactory(factory: TopChartSectionViewModel.Factory): AssistedViewModelFactory<*, *>

}