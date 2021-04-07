package com.caldeirasoft.outcast.ui.screen.podcastsettings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.airbnb.mvrx.MavericksViewModel
import com.caldeirasoft.outcast.domain.usecase.FetchStoreFrontUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadPodcastEpisodesUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadPodcastUseCase
import com.caldeirasoft.outcast.domain.usecase.LoadSettingsUseCase
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@OptIn(KoinApiExtension::class)
class PodcastSettingsViewModel(
    initialState: PodcastSettingsState,
) : MavericksViewModel<PodcastSettingsState>(initialState), KoinComponent {
    val fetchStoreFrontUseCase: FetchStoreFrontUseCase by inject()
    val loadPodcastUseCase: LoadPodcastUseCase by inject()
    val loadPodcastEpisodesUseCase: LoadPodcastEpisodesUseCase by inject()
    val loadSettingsUseCase: LoadSettingsUseCase by inject()

    val dataStore: DataStore<Preferences> = loadSettingsUseCase.settings
}