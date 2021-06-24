package com.caldeirasoft.outcast.ui.screen.played_episodes

import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListScreen
import cz.levinzonr.router.core.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Route(name = "played_episodes")
@Composable
fun PlayedEpisodesScreen(
    viewModel: PlayedEpisodesViewModel,
    navController: NavController,
) {
    EpisodeListScreen(
        title = stringResource(id = R.string.screen_played_episodes),
        viewModel = viewModel,
        navController = navController,
    )
}