package com.caldeirasoft.outcast.ui.screen.base

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Composable
inline fun <reified VM : BaseViewModel<State, Event, Action>, State : Any, Event : Any, Action : Any> Screen(
    viewModel: VM,
    crossinline onEvent: suspend (event: Event) -> Unit,
    content: @Composable (state: State, performAction: (action: Action) -> Unit) -> Unit
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { onEvent(it) }
    }
    val state = viewModel.state.collectAsState()
    DisposableEffect(viewModel) {
        viewModel.activate()
        onDispose {
        }
    }

    content(state.value) { action ->
        scope.launch {
            viewModel.performAction(action)
        }
    }
}
