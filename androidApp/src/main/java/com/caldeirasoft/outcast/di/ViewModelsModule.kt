package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.MavericksViewModelComponent
import com.caldeirasoft.outcast.di.hiltmavericks.ViewModelKey
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastViewModel
import com.caldeirasoft.outcast.ui.screen.store.discover.DiscoverViewModel
import com.caldeirasoft.outcast.ui.screen.store.topchartsection.TopChartEpisodeViewModel
import com.caldeirasoft.outcast.ui.screen.store.topchartsection.TopChartPodcastViewModel
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
    fun StorePodcastViewModelFactory(factory: PodcastViewModel.Factory): AssistedViewModelFactory<*, *>

    @[Binds IntoMap ViewModelKey(TopChartPodcastViewModel::class)]
    fun TopChartPodcastViewModelFactory(factory: TopChartPodcastViewModel.Factory): AssistedViewModelFactory<*, *>

    @[Binds IntoMap ViewModelKey(TopChartEpisodeViewModel::class)]
    fun TopChartEpisodeViewModelFactory(factory: TopChartEpisodeViewModel.Factory): AssistedViewModelFactory<*, *>
}