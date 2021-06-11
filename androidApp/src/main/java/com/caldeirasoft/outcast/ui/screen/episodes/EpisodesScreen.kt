package com.caldeirasoft.outcast.ui.screen.episodes.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeEvent
import com.caldeirasoft.outcast.ui.screen.episodes.*
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastEpisodesLoadingScreen
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDate
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
fun EpisodesScreen(
    viewModel: EpisodeListViewModel<EpisodesState, EpisodesEvent>,
    title: String,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
    onCategoryFilterClick: ((Category?) -> Unit)? = null,
) {
    val state by viewModel.state.collectAsState()
    val lazyPagingItems = viewModel.episodes.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    EpisodesScreen(
        state = state,
        scaffoldState = scaffoldState,
        title = title,
        lazyPagingItems = lazyPagingItems,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
        onCategoryFilterClick = onCategoryFilterClick,
        onEpisodeItemMoreButtonClick = { episode ->
            coroutineScope.OpenBottomSheetMenu(
                header = { // header : episode
                    EpisodeItem(
                        episode = episode,
                        showActions = false,
                    )
                },
                items = listOf(
                    BottomSheetMenuItem(
                        titleId = R.string.action_play_next,
                        icon = Icons.Default.QueuePlayNext,
                        onClick = { viewModel.playNext(episode) },
                    ),
                    BottomSheetMenuItem(
                        titleId = R.string.action_play_last,
                        icon = Icons.Default.AddToQueue,
                        onClick = { viewModel.playLast(episode) },
                    ),
                    if (episode.isSaved.not())
                        BottomSheetMenuItem(
                            titleId = R.string.action_save_episode,
                            icon = Icons.Outlined.BookmarkAdd,
                            onClick = { viewModel.saveEpisode(episode) },
                        )
                    else
                        BottomSheetMenuItem(
                            titleId = R.string.action_remove_saved_episode,
                            icon = Icons.Outlined.BookmarkRemove,
                            onClick = { viewModel.removeSavedEpisode(episode) },
                        )
                    ,
                    BottomSheetSeparator,
                    BottomSheetMenuItem(
                        titleId = R.string.action_share_episode,
                        icon = Icons.Default.Share,
                        onClick = { viewModel.shareEpisode(episode) },
                    )
                ),
                drawerState = drawerState,
                drawerContent = drawerContent
            )
        }
    )

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is EpisodesEvent.RefreshList ->
                    lazyPagingItems.refresh()
                is EpisodesEvent.PlayEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Play episode")
                is EpisodesEvent.PlayNextEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Play next episode")
                is EpisodesEvent.PlayLastEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Play last episode")
                is EpisodesEvent.DownloadEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Download episode")
                is EpisodesEvent.RemoveDownloadEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Remove download episode")
                is EpisodesEvent.CancelDownloadEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Cancel download episode")
                is EpisodesEvent.SaveEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Save episode")
                is EpisodesEvent.RemoveFromSavedEpisodesEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Remove from saved episode")
                is EpisodesEvent.ShareEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Share episode")
            }
        }
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun LatestEpisodesScreen(
    viewModel: LatestEpisodesViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    EpisodesScreen(
        title = stringResource(id = R.string.screen_latest_episodes),
        viewModel = viewModel,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
        onCategoryFilterClick = viewModel::filterByCategory
    )
}

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun SavedEpisodesScreen(
    viewModel: SavedEpisodesViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    EpisodesScreen(
        title = stringResource(id = R.string.screen_saved_episodes),
        viewModel = viewModel,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
    )
}

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun PlayedEpisodesScreen(
    viewModel: SavedEpisodesViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    EpisodesScreen(
        title = stringResource(id = R.string.screen_played_episodes),
        viewModel = viewModel,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EpisodesScreen(
    state: EpisodesState,
    scaffoldState: ScaffoldState,
    title: String,
    lazyPagingItems: LazyPagingItems<EpisodeUiModel>,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
    onEpisodeItemMoreButtonClick: (Episode) -> Unit,
    onCategoryFilterClick: ((Category?) -> Unit)? = null,
) {
    val context = LocalContext.current
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
            EpisodesTopAppBar(
                title = title,
                lazyListState = listState
            )
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
                    values = state.categories,
                    onClick = { category -> onCategoryFilterClick?.invoke(category) }) {
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
                                onEpisodeClick = {
                                    navigateTo(Screen.EpisodeScreen(uiModel.episode))
                                },
                                onContextMenuClick = {
                                    onEpisodeItemMoreButtonClick(uiModel.episode)
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
                        PodcastEpisodesLoadingScreen()
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
    lazyListState: LazyListState
) {
    Timber.d("progress: ${lazyListState.headerScrollRatio}, alpha:${lazyListState.topAppBarAlpha}")
    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
                AnimatedVisibility(
                    visible = (lazyListState.topAppBarAlpha == 1f),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Text(text = title)
                }
            },
            actions = { },
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface,
            elevation = 0.dp
        )

        if (lazyListState.topAppBarAlpha == 1f)
            Divider()
    }
}
