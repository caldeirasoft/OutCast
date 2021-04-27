package com.caldeirasoft.outcast.di

import com.caldeirasoft.outcast.di.hiltmavericks.AssistedViewModelFactory
import com.caldeirasoft.outcast.di.hiltmavericks.MavericksViewModelComponent
import com.caldeirasoft.outcast.di.hiltmavericks.ViewModelKey
import com.caldeirasoft.outcast.presentation.viewmodel.LibraryViewModel
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeViewModel
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

    @[Binds IntoMap ViewModelKey(LibraryViewModel::class)]
    fun LibraryViewModelFactory(factory: LibraryViewModel.Factory): AssistedViewModelFactory<*, *>
}