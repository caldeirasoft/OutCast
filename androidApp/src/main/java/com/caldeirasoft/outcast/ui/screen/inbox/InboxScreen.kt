package com.caldeirasoft.outcast.ui.screen.episodelist.base

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListScreen
import com.caldeirasoft.outcast.ui.screen.inbox.InboxViewModel
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
    EpisodeListScreen(
        title = stringResource(id = R.string.screen_inbox),
        viewModel = viewModel,
        navController = navController,
        hideTopBar = true
    )
}
