package com.caldeirasoft.outcast.ui.screen.episodes.latest

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episodes.base.EpisodesScreen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastEpisodesLoadingScreen
import com.caldeirasoft.outcast.ui.util.ifLoading
import com.caldeirasoft.outcast.ui.util.rememberLazyListStateWithPagingItems
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun LatestEpisodesScreen(
    viewModel: LatestEpisodesViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    EpisodesScreen(
        viewModel = viewModel,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}
