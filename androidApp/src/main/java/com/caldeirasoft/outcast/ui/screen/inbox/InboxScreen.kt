package com.caldeirasoft.outcast.ui.screen.episodes.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.screen.inbox.InboxViewModel
import com.caldeirasoft.outcast.ui.util.navigateToEpisode
import com.caldeirasoft.outcast.ui.util.navigateToPodcast
import cz.levinzonr.router.core.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
@Route(name = "inbox")
@Composable
fun InboxScreen(
    viewModel: InboxViewModel,
    navController: NavController,
) {
    EpisodesScreen(
        title = stringResource(id = R.string.screen_inbox),
        viewModel = viewModel,
        navigateToPodcast = { navController.navigateToPodcast(it) },
        navigateToEpisode = { navController.navigateToEpisode(it) },
        navigateUp = { navController.navigateUp() },
        onCategoryFilterClick = viewModel::filterByCategory,
        hideTopBar = true
    )
}
