package com.caldeirasoft.outcast.ui.screen.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.caldeirasoft.outcast.data.db.entities.PodcastSettings
import com.caldeirasoft.outcast.data.db.entities.Settings
import com.caldeirasoft.outcast.data.repository.SettingsRepository
import com.caldeirasoft.outcast.ui.screen.base.BaseViewModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.Podcast_settingsRouteArgs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
) : BaseViewModel<SettingsViewModel.State, SettingsViewModel.Event, SettingsViewModel.Action>(
    initialState = State()
) {

    override fun activate() {
        settingsRepository.getSettings().setOnEach {
            copy(settings = it)
        }
    }

    override suspend fun performAction(action: Action) = when (action) {
        is Action.UpdateSettings -> updateSettings(action.settings)
        is Action.Exit -> emitEvent(Event.Exit)
    }

    private fun updateSettings(settings: Settings) {
        viewModelScope.launch {
            settingsRepository.updateSettings(settings)
        }
    }

    data class State(
        val settings: Settings? = null,
    )

    sealed class Event {
        object Exit : Event()
    }

    sealed class Action {
        data class UpdateSettings(val settings: Settings) : Action()
        object Exit : Action()
    }
}