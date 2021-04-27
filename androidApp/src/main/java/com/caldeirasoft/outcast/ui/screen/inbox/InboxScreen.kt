package com.caldeirasoft.outcast.ui.screen.inbox

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.ui.components.EpisodeItem
import com.caldeirasoft.outcast.ui.components.ReachableAppBar
import com.caldeirasoft.outcast.ui.components.ReachableScaffold
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.toDp
import kotlinx.coroutines.FlowPreview
import com.caldeirasoft.outcast.R as R1

@FlowPreview
@Composable
fun InboxScreen(
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: InboxViewModel = mavericksViewModel()
    val state by viewModel.collectAsState()

    InboxScreen(
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun InboxScreen(
    state: InboxViewState,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)) {

            item {
                Spacer(modifier = Modifier.height(spacerHeight.toDp()))
            }

            items(items = state.episodes) { episode ->
                EpisodeItem(
                    episode = episode,
                    onEpisodeClick = {
                        navigateTo(Screen.EpisodeScreen(episode))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        ReachableAppBar(
            title = { Text(text = stringResource(id = R1.string.screen_inbox)) },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                    )
                }
            },
            state = listState,
            headerHeight = headerHeight)
    }
}
