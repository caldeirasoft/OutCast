package com.caldeirasoft.outcast.ui.screen.episodelist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.QueuePlayNext
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.screen.base.Screen
import com.caldeirasoft.outcast.ui.screen.saved_episodes.SavedEpisodesViewModel
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDate
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import cz.levinzonr.router.core.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun EpisodeListScreen(
    navController: NavController,
    viewModel: EpisodeListViewModel<EpisodeListViewModel.State, EpisodeListViewModel.Event, EpisodeListViewModel.Action>,
    title: String,
    hideTopBar: Boolean = false
) {
    val scaffoldState = rememberScaffoldState()
    val lazyPagingItems = viewModel.episodes.collectAsLazyPagingItems()
    Screen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is EpisodeListViewModel.Event.OpenPodcastDetail ->
                    navController.navigateToPodcast(event.episode.feedUrl)
                is EpisodeListViewModel.Event.OpenEpisodeDetail ->
                    navController.navigateToEpisode(event.episode)
                is EpisodeListViewModel.Event.Exit ->
                    navController.navigateUp()
                is EpisodeListViewModel.Event.RefreshList ->
                    lazyPagingItems.refresh()
                is EpisodeListViewModel.Event.ShareEpisode ->
                    //Toast.makeText()
                    scaffoldState.snackbarHostState.showSnackbar("Share episode")
            }
        }
    ) { state, performAction ->

        EpisodeListScreen(
            state = state,
            scaffoldState = scaffoldState,
            title = title,
            lazyPagingItems = lazyPagingItems,
            performAction = performAction,
            hideTopBar = hideTopBar,
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EpisodeListScreen(
    state: EpisodeListViewModel.State,
    scaffoldState: ScaffoldState,
    title: String,
    lazyPagingItems: LazyPagingItems<EpisodeUiModel>,
    performAction: (EpisodeListViewModel.Action) -> Unit,
    hideTopBar: Boolean = false
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val listState = lazyPagingItems.rememberLazyListStateWithPagingItems()
    ScaffoldWithLargeHeader(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        scaffoldState = scaffoldState,
        listState = listState,
        snackbarHost = {
            // reuse default SnackbarHost to have default animation and timing handling
            SnackbarHost(it) { data ->
                // custom snackbar with the custom border
                Snackbar(
                    modifier = Modifier.padding(bottom = 56.dp),
                    snackbarData = data
                )
            }
        },
        topBar = {
            if (!hideTopBar) {
                EpisodesTopAppBar(
                    modifier = Modifier,
                    title = title,
                    lazyListState = listState,
                    navigateUp = { performAction(EpisodeListViewModel.Action.Exit) }
                )
            }
        }
    ) { headerHeight ->
        LazyColumn(state = listState) {
            // header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = headerHeight.toDp())
                ) {
                    Text(
                        text = title,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(top = 16.dp, bottom = 16.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        style = typography.h4
                    )
                }
            }

            // filter : categories (if exist)
            item {
                ChipGroup(
                    selectedValue = state.category,
                    values = state.categories.sortedBy { it.text },
                    onClick = { category ->
                        performAction(EpisodeListViewModel.Action.FilterCategory(category))
                    }) {
                    Text(text = it.text)
                }
            }
            // episodes
            items(lazyPagingItems = lazyPagingItems) { uiModel ->
                uiModel?.let {
                    when (uiModel) {
                        is EpisodeUiModel.EpisodeItem -> {
                            EpisodeItem(
                                episode = uiModel.episode,
                                download = state.downloads.firstOrNull { it.url == uiModel.episode.mediaUrl },
                                onPodcastClick = {
                                    performAction(EpisodeListViewModel.Action.OpenPodcastDetail(uiModel.episode))
                                },
                                onEpisodeClick = {
                                    performAction(EpisodeListViewModel.Action.OpenEpisodeDetail(uiModel.episode))
                                },
                                onContextMenuClick = {
                                    coroutineScope.OpenBottomSheetMenu(
                                        header = { // header : episode
                                            EpisodeItem(
                                                episode = uiModel.episode,
                                                showActions = false,
                                            )
                                        },
                                        items = listOf(
                                            BottomSheetMenuItem(
                                                titleId = R.string.action_play_next,
                                                icon = Icons.Default.QueuePlayNext,
                                                onClick = { performAction(EpisodeListViewModel.Action.PlayNextEpisode(uiModel.episode)) },
                                            ),
                                            BottomSheetMenuItem(
                                                titleId = R.string.action_play_last,
                                                icon = Icons.Default.AddToQueue,
                                                onClick = { performAction(EpisodeListViewModel.Action.PlayLastEpisode(uiModel.episode)) },
                                            ),
                                            if (uiModel.episode.isSaved.not())
                                                BottomSheetMenuItem(
                                                    titleId = R.string.action_save_episode,
                                                    icon = Icons.Outlined.BookmarkAdd,
                                                    onClick = { performAction(EpisodeListViewModel.Action.ToggleSaveEpisode(uiModel.episode)) },
                                                )
                                            else
                                                BottomSheetMenuItem(
                                                    titleId = R.string.action_remove_saved_episode,
                                                    icon = Icons.Outlined.BookmarkRemove,
                                                    onClick = { performAction(EpisodeListViewModel.Action.ToggleSaveEpisode(uiModel.episode)) },
                                                ),
                                            BottomSheetSeparator,
                                            BottomSheetMenuItem(
                                                titleId = R.string.action_share_episode,
                                                icon = Icons.Default.Share,
                                                onClick = { performAction(EpisodeListViewModel.Action.ShareEpisode(uiModel.episode)) },
                                            )
                                        ),
                                        drawerState = drawerState,
                                        drawerContent = drawerContent
                                    )
                                }
                            )
                            Divider()
                        }
                        is EpisodeUiModel.SeparatorItem ->
                            Text(
                                text = uiModel.date.formatRelativeDate(context),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.body2
                            )
                    }
                }
            }

            lazyPagingItems
                .ifLoading {
                    item {
                        LoadingScreen()
                    }
                }

            item {
                // bottom app bar spacer
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EpisodesTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    lazyListState: LazyListState,
    navigateUp: () -> Unit,
) {
    Timber.d("progress: ${lazyListState.headerScrollRatio}, alpha:${lazyListState.topAppBarAlpha}")
    val appBarAlpha = lazyListState.topAppBarAlpha
    val backgroundColor: Color = Color.blendARGB(
        MaterialTheme.colors.surface.copy(alpha = 0f),
        MaterialTheme.colors.surface,
        appBarAlpha)
    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
                AnimatedVisibility(
                    visible = (appBarAlpha == 1f),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(text = title)
                }
            },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            },

            actions = { },
            backgroundColor = backgroundColor,
            contentColor = MaterialTheme.colors.onSurface,
            elevation = 0.dp
        )

        if (appBarAlpha == 1f)
            Divider()
    }
}
