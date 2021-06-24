package com.caldeirasoft.outcast.ui.screen.saved_episodes

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
@Route(name = "saved_episodes")
@Composable
fun SavedEpisodesScreen(
    viewModel: SavedEpisodesViewModel,
    navController: NavController,
) {
    EpisodeListScreen(
        title = stringResource(id = R.string.screen_saved_episodes),
        viewModel = viewModel,
        navController = navController,
    )
}