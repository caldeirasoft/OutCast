package com.caldeirasoft.outcast.ui.screen.library

import androidx.annotation.StringRes
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
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.presentation.viewmodel.LibraryViewModel
import com.caldeirasoft.outcast.ui.components.PodcastDefaults
import com.caldeirasoft.outcast.ui.components.PodcastThumbnail
import com.caldeirasoft.outcast.ui.components.ScaffoldWithLargeHeader
import com.caldeirasoft.outcast.ui.components.bottomsheet.BottomSheetMenuItem
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.bottomsheet.OpenBottomSheetMenu
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.CollapsingToolbarState
import com.caldeirasoft.outcast.ui.components.gridItems
import com.caldeirasoft.outcast.ui.screen.base.Screen
import com.caldeirasoft.outcast.ui.screen.search_results.SearchResultsViewModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.RoutesActions
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDate
import com.caldeirasoft.outcast.ui.util.navigateToEpisode
import com.caldeirasoft.outcast.ui.util.navigateToPodcast
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import cz.levinzonr.router.core.Route
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

enum class LibraryItemType (
    @StringRes val titleId: Int,
    val action: LibraryViewModel.Action? = null,
) {
    SAVED_EPISODES(R.string.library_item_saved, LibraryViewModel.Action.OpenSavedEpisodes),
    PLAYED_EPISODES(R.string.library_item_played, LibraryViewModel.Action.OpenPlayedEpisodes),
    SIDELOADS(R.string.library_item_sideloads, LibraryViewModel.Action.OpenSideLoads),
    PODCASTS(R.string.library_item_podcasts)
}

@FlowPreview
@ExperimentalCoroutinesApi
@Route(name = "library")
@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel,
    navController: NavController,
) {
    Screen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is LibraryViewModel.Event.OpenPodcastDetail ->
                    navController.navigateToPodcast(event.podcast)
                is LibraryViewModel.Event.OpenSavedEpisodes ->
                    navController.navigate(RoutesActions.toSaved_episodes())
                is LibraryViewModel.Event.OpenPlayedEpisodes ->
                    navController.navigate(RoutesActions.toPlayed_episodes())
            }
        }
    ) { state, performAction ->
        LibraryScreen(
            state = state,
            performAction = performAction
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun LibraryScreen(
    state: LibraryViewModel.State,
    performAction: (LibraryViewModel.Action) -> Unit
) {
    ScaffoldWithLargeHeader(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) { headerHeight ->
        LazyColumn {
            // header
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height = headerHeight.toDp())
                ) {
                    Text(
                        text = stringResource(id = R.string.screen_library),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(top = 16.dp, bottom = 16.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        fontSize = 36.sp
                    )
                }
            }

            val libraryIds = listOf(
                LibraryItemType.SAVED_EPISODES.name
            ) + state.sortedPodcasts.map { it.feedUrl }

            val libraryItemsMap = LibraryItemType.values().map { it.name to it }.toMap()
            val podcastsMap = state.sortedPodcasts.map { it.feedUrl to it }.toMap()

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // sort button
                    LibrarySortButton(
                        state = state,
                        performAction = performAction,
                    )
                    // spacer
                    Spacer(Modifier.weight(1f))
                    // grid/list button
                    LibraryDisplayButton(
                        state = state,
                        onClick = { performAction(LibraryViewModel.Action.ToggleDisplay) }
                    )
                }
            }

            if (state.displayAsGrid) {
                gridItems(
                    items = libraryIds,
                    contentPadding = PaddingValues(16.dp),
                    horizontalInnerPadding = 16.dp,
                    verticalInnerPadding = 16.dp,
                    columns = 2
                ) { itemId ->
                    when (itemId) {
                        LibraryItemType.SIDELOADS.name,
                        LibraryItemType.SAVED_EPISODES.name ->
                            libraryItemsMap[itemId]?.let { item ->
                                LibraryGridItem(
                                    item = item,
                                    state = state,
                                    performAction = performAction
                                )
                            }
                        else -> {
                            podcastsMap[itemId]?.let { item ->
                                PodcastGridItem(
                                    podcast = item,
                                    sort = state.sortBy,
                                    onClick = {
                                        performAction(LibraryViewModel.Action.OpenPodcastDetail(item))
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                items(items = libraryIds) { itemId ->
                    when (itemId) {
                        LibraryItemType.SIDELOADS.name,
                        LibraryItemType.SAVED_EPISODES.name ->
                            libraryItemsMap[itemId]?.let { item ->
                                LibraryListItem(
                                    item = item,
                                    state = state,
                                    performAction = performAction
                                )
                            }
                        else -> {
                            podcastsMap[itemId]?.let { item ->
                                PodcastListItem(
                                    podcast = item,
                                    sort = state.sortBy,
                                    onClick = {
                                        performAction(LibraryViewModel.Action.OpenPodcastDetail(item))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                // bottom app bar spacer
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }
}

@Composable
private fun LibraryTopAppBar(
    modifier: Modifier,
    state: LibraryState,
    collapsingToolbarState: CollapsingToolbarState,
    onPlayedEpisodesButtonClick: () -> Unit,
) {
    Timber.d("progress: ${collapsingToolbarState.progress}, alpha:${collapsingToolbarState.collapsedAlpha}")
    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
            },
            actions = {
                IconButton(onClick = onPlayedEpisodesButtonClick) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                    )
                }
            },
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colors.onSurface,
            elevation = 0.dp
        )

        if (collapsingToolbarState.progress == 0f)
            Divider()
    }
}

@Composable
private fun PodcastGridItem(
    modifier: Modifier = Modifier,
    podcast: Podcast,
    sort: LibrarySort,
    onClick: () -> Unit
)
{
    val context = LocalContext.current
    Column(modifier = modifier) {
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp),
            onClick = onClick
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
    state: LibraryViewModel.State,
    performAction: (LibraryViewModel.Action) -> Unit
) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = {
            item.action?.let {
                performAction(item.action)
            }
        })
    ) {
        LibraryThumbnail(item = item, modifier = Modifier)
        Text(
            text = stringResource(id = item.titleId),
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 2,
            style = MaterialTheme.typography.body1
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            LibraryListItemSecondaryText(item, state)
        }
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
    sort: LibrarySort,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
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
    state: LibraryViewModel.State,
    performAction: (LibraryViewModel.Action) -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = {
                item.action?.let {
                    performAction(item.action)
                }
            }),
        text = {
            Text(
                text = stringResource(id = item.titleId),
                maxLines = 2
            )
        },
        secondaryText = {
            LibraryListItemSecondaryText(item, state)
        },
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
private fun LibraryListItemSecondaryText(
    item: LibraryItemType,
    state: LibraryViewModel.State,
) {
    when (item) {
        LibraryItemType.SAVED_EPISODES -> {
            Text(
                text = stringResource(
                    id = R.string.podcast_x_episodes,
                    state.savedEpisodesCount
                )
            )
        }
        else -> null
    }
}

@Composable
private fun LibrarySortButton(
    state: LibraryViewModel.State,
    performAction: (LibraryViewModel.Action) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val imageVector =
        if (state.sortByDesc) Icons.Default.ArrowDownward
        else Icons.Default.ArrowUpward
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(onClick = {
                coroutineScope.OpenBottomSheetMenu(
                    header = { // header : podcast
                        Text(
                            text = stringResource(id = R.string.action_sort_by),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    },
                    items = LibrarySort
                        .values()
                        .map { sort ->
                            BottomSheetMenuItem(
                                titleId = sort.titleId,
                                icon = sort
                                    .takeIf { it == state.sortBy }
                                    ?.let {
                                        when (state.sortByDesc) {
                                            true -> Icons.Default.ArrowDownward
                                            false -> Icons.Default.ArrowUpward
                                        }
                                    },
                                onClick = {
                                    coroutineScope.launch { drawerState.hide() }
                                    if (sort == state.sortBy)
                                        performAction(LibraryViewModel.Action.ChangeSortOrder(!state.sortByDesc))
                                    else
                                        performAction(LibraryViewModel.Action.ChangeSort(sort))
                                },
                            )
                        },
                    drawerState = drawerState,
                    drawerContent = drawerContent
                )
            })
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
    state: LibraryViewModel.State,
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

