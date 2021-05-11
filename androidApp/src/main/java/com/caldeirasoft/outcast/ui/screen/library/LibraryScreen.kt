package com.caldeirasoft.outcast.ui.screen.library

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.presentation.viewmodel.LibraryViewModel
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDate
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDateTime
import com.caldeirasoft.outcast.ui.util.applyTextStyleCustom
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    LibraryScreen(
        state = state,
    ) { action ->
        when (action) {
            is LibraryActions.OpenPodcastDetail -> navigateTo(Screen.PodcastScreen(action.podcast))
            is LibraryActions.OpenNewEpisodesScreen -> navigateTo(Screen.Inbox)
            is LibraryActions.OpenSavedEpisodesScreen -> navigateTo(Screen.Inbox)
            is LibraryActions.OpenHistoryScreen -> navigateTo(Screen.Inbox)
            else -> viewModel.submitAction(action)
        }
    }

    ObsertUiEffects(
        viewModel = viewModel,
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LibraryScreen(
    state: LibraryState,
    actioner : (LibraryActions) -> Unit,
) {
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
                    var paddingStart by remember { mutableStateOf(16.dp) }

                    Text(
                        text = stringResource(id = R.string.screen_library),
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

                    LibraryTopAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .pin()
                            .statusBarsPadding()
                            .navigationBarsPadding(bottom = false),
                        state = state,
                        collapsingToolbarState = collapsingToolbarState
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .appBarBody()
                )
                {
                    val libraryIds = listOf(
                        LibraryItemType.LATEST_EPISODES.name,
                        LibraryItemType.SAVED_EPISODES.name
                    ) + state.sortedPodcasts.map { it.feedUrl }

                    val libraryItemsMap = LibraryItemType.values().map { it.name to it }.toMap()
                    val podcastsMap = state.sortedPodcasts.map { it.feedUrl to it }.toMap()

                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // sort button
                            LibrarySortButton(state = state, onClick = {
                                actioner(LibraryActions.OpenSortByBottomSheet)
                            })
                            // spacer
                            Spacer(Modifier.weight(1f))
                            // grid/list button
                            LibraryDisplayButton(state = state, onClick = {
                                actioner(LibraryActions.ToggleDisplay)
                            })
                        }
                    }

                    when (state.displayAsGrid) {
                        false -> items(items = libraryIds) { itemId ->
                            when (itemId) {
                                LibraryItemType.LATEST_EPISODES.name,
                                LibraryItemType.SAVED_EPISODES.name ->
                                    libraryItemsMap[itemId]?.let { item ->
                                        LibraryListItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(onClick = { }),
                                            item = item
                                        )
                                    }
                                else -> {
                                    podcastsMap[itemId]?.let { item ->
                                        PodcastListItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(onClick = {
                                                    actioner(
                                                        LibraryActions.OpenPodcastDetail(
                                                            item
                                                        )
                                                    )
                                                }),
                                            podcast = item,
                                            sort = state.sortBy
                                        )
                                    }
                                }
                            }
                        }

                        true -> gridItems(
                            items = libraryIds,
                            contentPadding = PaddingValues(16.dp),
                            horizontalInnerPadding = 16.dp,
                            verticalInnerPadding = 16.dp,
                            columns = 2
                        ) { itemId ->
                            when (itemId) {
                                LibraryItemType.LATEST_EPISODES.name,
                                LibraryItemType.SAVED_EPISODES.name ->
                                    libraryItemsMap[itemId]?.let { item ->
                                        LibraryGridItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(onClick = { }),
                                            item = item
                                        )
                                    }
                                else -> {
                                    podcastsMap[itemId]?.let { item ->
                                        PodcastGridItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(onClick = {
                                                    actioner(
                                                        LibraryActions.OpenPodcastDetail(
                                                            item
                                                        )
                                                    )
                                                }),
                                            podcast = item,
                                            sort = state.sortBy
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LibraryTopAppBar(
    modifier: Modifier,
    state: LibraryState,
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

@Composable
fun PodcastGridItem(
    modifier: Modifier = Modifier,
    podcast: Podcast,
    sort: LibrarySort,
)
{
    val context = LocalContext.current
    Column(modifier = modifier) {
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp)
        )
        {
            Image(
                painter = rememberCoilPainter(request = podcast.artworkUrl),
                contentDescription = podcast.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f))
        }
        Text(
            text = podcast.name,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.body1
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = when (sort) {
                    LibrarySort.RECENTLY_FOLLOWED ->
                        podcast.followedAt?.formatRelativeDate(context).orEmpty()
                    LibrarySort.RECENTLY_UPDATED ->
                        podcast.releaseDateTime.formatRelativeDate(context)
                    else -> podcast.artistName
                },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1, overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1
            )
        }
    }
}


@Composable
fun LibraryGridItem(
    item: LibraryItemType,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        LibraryThumbnail(item = item, modifier = Modifier)
        Text(
            text = stringResource(id = item.titleId),
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.body1
        )
        /*Text(
            podcast.artistName,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1, overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.caption
        )*/
    }
}

@Composable
private fun PodcastListItem(
    modifier: Modifier = Modifier,
    podcast: Podcast,
    sort: LibrarySort
) {
    val context = LocalContext.current
    ListItem(
        modifier = modifier.fillMaxWidth(),
        text = {
            Text(text = podcast.name,
                maxLines = 2
            )
        },
        secondaryText = {
            Text(text = when (sort) {
                LibrarySort.RECENTLY_FOLLOWED ->
                    podcast.followedAt?.formatRelativeDate(context).orEmpty()
                LibrarySort.RECENTLY_UPDATED ->
                    podcast.releaseDateTime.formatRelativeDate(context)
                else -> podcast.artistName
            })
        },
        icon = {
            PodcastThumbnail(
                data = podcast.artworkUrl,
                modifier = Modifier.size(PodcastDefaults.ThumbnailSize)
            )
        },
    )
}

@Composable
fun LibraryListItem(
    item: LibraryItemType,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier.fillMaxWidth(),
        text = {
            Text(text = stringResource(id = item.titleId),
                maxLines = 2
            )
        },
        secondaryText = {  },
        icon = {
            LibraryThumbnail(
                item = item,
                modifier = Modifier.size(PodcastDefaults.ThumbnailSize)
            )
        },
    )
}

@Composable
private fun LibraryThumbnail(
    item: LibraryItemType,
    modifier: Modifier = Modifier
) {
    Card(
        backgroundColor = colors[1],
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxSize()
            .aspectRatio(1f)
    )
    {
        Box(modifier = Modifier.fillMaxSize(0.5f)) {
            Icon(
                imageVector = when(item) {
                    LibraryItemType.LATEST_EPISODES -> Icons.Filled.Schedule
                    LibraryItemType.SAVED_EPISODES -> Icons.Filled.BookmarkBorder
                    LibraryItemType.SIDELOADS -> Icons.Filled.Cloud
                    else -> Icons.Filled.Podcasts
                },
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxSize(0.5f)
            )
        }
    }
}

@Composable
private fun LibrarySortButton(
    state: LibraryState,
    onClick : () -> Unit
) {
    val imageVector =
        if (state.sortByDesc) Icons.Default.ArrowDownward
        else Icons.Default.ArrowUpward
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Text(text = stringResource(id = state.sortBy.titleId))
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            modifier = Modifier
                .padding(start = 4.dp)
                .size(20.dp)
        )
    }
}

@Composable
private fun LibraryDisplayButton(
    state: LibraryState,
    onClick : () -> Unit
) {
    val imageVector =
        if (state.displayAsGrid) Icons.Filled.List
        else Icons.Filled.ViewModule
    IconButton(onClick = onClick) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
        )
    }
}

@Composable
private fun ObsertUiEffects(
    viewModel: LibraryViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is LibraryEvent.OpenSortByBottomSheet -> {
                    OpenBottomSheetMenu(
                        header = { // header : podcast
                            Text(
                                text = stringResource(id = R.string.action_sort_by),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        },
                        items = LibrarySort.values().map { sort ->
                            BottomSheetMenuItem(
                                titleId = sort.titleId,
                                icon = sort
                                    .takeIf { it == event.sort }
                                    ?.let {
                                        when (event.sortByDesc) {
                                            true -> Icons.Default.ArrowDownward
                                            false -> Icons.Default.ArrowUpward
                                        }
                                    },
                                onClick = {
                                    coroutineScope.launch { drawerState.hide() }
                                    if (sort == event.sort)
                                        viewModel.submitAction(LibraryActions.ChangeSortOrder(!event.sortByDesc))
                                    else
                                        viewModel.submitAction(LibraryActions.ChangeSort(sort))
                                },
                            )
                        },
                        drawerState = drawerState,
                        drawerContent = drawerContent
                    )
                }
            }
        }
    }
}
