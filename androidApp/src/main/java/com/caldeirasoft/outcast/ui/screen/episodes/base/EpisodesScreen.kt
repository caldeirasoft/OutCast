package com.caldeirasoft.outcast.ui.screen.episodes.base

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
import androidx.compose.ui.platform.LocalContext
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
import com.caldeirasoft.outcast.ui.components.foundation.FilterChipGroup
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastEpisodesLoadingScreen
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDate
import com.caldeirasoft.outcast.ui.util.ifLoading
import com.caldeirasoft.outcast.ui.util.rememberLazyListStateWithPagingItems
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun EpisodesScreen(
    viewModel: EpisodesViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val lazyPagingItems = viewModel.episodes.collectAsLazyPagingItems()

    EpisodesScreen(
        state = state,
        lazyPagingItems = lazyPagingItems
    ) { action ->
        when (action) {
            is EpisodesActions.NavigateUp -> navigateBack()
            is EpisodesActions.OpenEpisodeDetail ->
                navigateTo(Screen.EpisodeScreen(
                    action.episode.feedUrl,
                    action.episode.guid,
                    false))
            else -> viewModel.submitAction(action)
        }
    }

    ObsertUiEffects(
        viewModel = viewModel,
        lazyPagingItems = lazyPagingItems
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun EpisodesScreen(
    state: EpisodesState,
    lazyPagingItems: LazyPagingItems<EpisodesUiModel>,
    actioner : (EpisodesActions) -> Unit,
) {
    val context = LocalContext.current
    Scaffold(modifier = Modifier) {
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight
            val headerRatio: Float = 1 / 3f
            val headerHeight = remember { mutableStateOf((screenHeight * headerRatio).toInt()) }

            val collapsingToolbarState = rememberCollapsingToolbarState()
            AppbarContainer(
                modifier = Modifier.fillMaxWidth(),
                scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
                collapsingToolbarState = collapsingToolbarState
            ) {
                CollapsingToolbar(collapsingToolbarState = collapsingToolbarState) {
                    var textSize by remember { mutableStateOf(25.sp) }

                    Text(
                        text = stringResource(id = R.string.screen_latest_episodes),
                        modifier = Modifier
                            .heightIn(min = AppBarHeight)
                            .road(Alignment.CenterStart, Alignment.BottomStart)
                            .progress { value ->
                                textSize = (18 + (36 - 18) * value).sp
                            }
                            .padding(top = 16.dp, bottom = 16.dp)
                            .padding(start = 16.dp, end = 16.dp)
                            .statusBarsPadding(),
                        fontSize = textSize
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = headerHeight.value.toDp())
                            .pin()
                    ) {
                    }

                    EpisodesTopAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pin()
                            .statusBarsPadding()
                            .navigationBarsPadding(bottom = false),
                        state = state,
                        collapsingToolbarState = collapsingToolbarState
                    )
                }

                val listState = lazyPagingItems.rememberLazyListStateWithPagingItems()
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .appBarBody()
                ) {
                    // filter : categories (if exist)
                    item {
                        ChipGroup(
                            selectedValue = state.category,
                            values = state.categories,
                            onClick = { actioner(EpisodesActions.FilterByCategory(it)) }) {
                            Text(text = it.text)
                        }
                    }
                    // episodes
                    items(lazyPagingItems = lazyPagingItems) { uiModel ->
                        uiModel?.let {
                            when(uiModel) {
                                is EpisodesUiModel.EpisodeItem -> {
                                    EpisodeItem(
                                        episode = uiModel.episode,
                                        onEpisodeClick = {
                                            actioner(
                                                EpisodesActions.OpenEpisodeDetail(uiModel.episode)
                                            )
                                        },
                                        onContextMenuClick = {
                                            actioner(
                                                EpisodesActions.OpenEpisodeContextMenu(uiModel.episode)
                                            )
                                        }
                                    )
                                    Divider()
                                }
                                is EpisodesUiModel.SeparatorItem ->
                                    Text(text = uiModel.date.formatRelativeDate(context),
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
    }
}

@Composable
private fun EpisodesTopAppBar(
    modifier: Modifier,
    state: EpisodesState,
    collapsingToolbarState: CollapsingToolbarState
) {
    Timber.d("progress: ${collapsingToolbarState.progress}, alpha:${collapsingToolbarState.collapsedAlpha}")
    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
            },
            actions = { },
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface,
            elevation = 0.dp
        )

        if (collapsingToolbarState.progress == 0f)
            Divider()
    }
}

@OptIn(InternalCoroutinesApi::class)
@Composable
private fun ObsertUiEffects(
    viewModel: EpisodesViewModel,
    lazyPagingItems: LazyPagingItems<*>
) {
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is EpisodesEvent.RefreshList ->
                    lazyPagingItems.refresh()
                is EpisodesEvent.OpenEpisodeContextMenu -> {
                    OpenBottomSheetMenu(
                        header = { // header : episode
                            EpisodeItem(
                                episode = event.episode,
                                showActions = false,
                            )
                        },
                        items = listOf(
                            BottomSheetMenuItem(
                                titleId = R.string.action_play_next,
                                icon = Icons.Default.QueuePlayNext,
                                onClick = { viewModel.submitAction(EpisodesActions.PlayNextEpisode(event.episode)) },
                            ),
                            BottomSheetMenuItem(
                                titleId = R.string.action_play_last,
                                icon = Icons.Default.AddToQueue,
                                onClick = { viewModel.submitAction(EpisodesActions.PlayLastEpisode(event.episode)) },
                            ),
                            BottomSheetMenuItem(
                                titleId = R.string.action_save_episode,
                                icon = Icons.Default.FavoriteBorder,
                                onClick = { viewModel.submitAction(EpisodesActions.SaveEpisode(event.episode)) },
                            ),
                            BottomSheetSeparator,
                            BottomSheetMenuItem(
                                titleId = R.string.action_share_episode,
                                icon = Icons.Default.Share,
                                onClick = { viewModel.submitAction(EpisodesActions.ShareEpisode(event.episode)) },
                            )
                        ),
                        drawerState = drawerState,
                        drawerContent = drawerContent
                    )
                }
            }
        }
    }
}
