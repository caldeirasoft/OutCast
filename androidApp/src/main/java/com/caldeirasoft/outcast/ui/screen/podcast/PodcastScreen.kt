package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.enums.SortOrder
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StoreData.Companion.toStoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.base.Screen
import com.caldeirasoft.outcast.ui.screen.episodelist.EpisodeListViewModel
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.storedata.*
import com.caldeirasoft.outcast.ui.screen.store.storedata.args.Podcast_settingsRouteArgs
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import cz.levinzonr.router.core.Route
import cz.levinzonr.router.core.RouteArg
import cz.levinzonr.router.core.RouteArgType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi

@OptIn(ExperimentalAnimationApi::class, FlowPreview::class, InternalCoroutinesApi::class)
@Route(
    name = "podcast",
    args = [
        RouteArg("feedUrl", RouteArgType.StringType, false),
    ]
)
@Composable
fun PodcastScreen(
    viewModel: PodcastViewModel,
    storePodcast: StorePodcast? = null,
    navController: NavController,
) {
    val scaffoldState = rememberScaffoldState()
    val lazyPagingItems = viewModel.episodes.collectAsLazyPagingItems()

    storePodcast?.let {
        LaunchedEffect(storePodcast) {
            viewModel.setPodcast(it)
        }
    }

    Screen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is PodcastViewModel.Event.OpenPodcastDetail ->
                    navController.navigateToPodcast(event.episode.feedUrl)
                is PodcastViewModel.Event.OpenEpisodeDetail ->
                    navController.navigateToEpisode(event.episode)
                is PodcastViewModel.Event.OpenSettings ->
                    navController.navigateToPodcastSettings(event.feedUrl)
                is PodcastViewModel.Event.Exit ->
                    navController.navigateUp()
            }
        }
    ) { state, performAction ->
        PodcastScreen(
            state = state,
            scaffoldState = scaffoldState,
            lazyPagingItems = lazyPagingItems,
            performAction = performAction
        )
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@ExperimentalCoroutinesApi
@Composable
private fun PodcastScreen(
    state: PodcastViewModel.State,
    scaffoldState: ScaffoldState,
    lazyPagingItems: LazyPagingItems<EpisodeUiModel>,
    performAction: (PodcastViewModel.Action) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val listState = lazyPagingItems.rememberLazyListStateWithPagingItems()

    val podcastData = state.podcast

    ScaffoldWithLargeHeaderAndLazyColumn(
        listState = listState,
        headerRatio = 1 / 2f,
        scaffoldState = scaffoldState,
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
            PodcastScreenTopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                state = state,
                lazyListState = listState,
                navigateUp = { performAction(PodcastViewModel.Action.Exit) }
            )
        },
        headerContent = {
            /*val artwork = state.storeData.artwork
            if (artwork != null) {
                HeaderEditorialArtwork(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(listState.expandedHeaderAlpha),
                    artwork = artwork
                )
            } else {*/
            PodcastHeader(
                modifier = Modifier
                    .fillMaxSize(),
                state = state,
                lazyListState = listState,
                openStoreDataDetail = { performAction(PodcastViewModel.Action.OpenStoreData(it)) }
            )
            //}
        }
    ) {
        // action buttons
        item {
            podcastData?.let {
                PodcastActionAppBar(
                    state = state,
                    performAction = performAction,
                )
            }
        }
        // description
        item {
            PodcastDescriptionContent(description = podcastData?.description)
        }

        // genre
        item {
            podcastData?.genre?.let { genre ->
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 8.dp)
                ) {
                    ChipButton(selected = false,
                        onClick = {
                            performAction(PodcastViewModel.Action.OpenStoreData(genre.toStoreData()))
                        })
                    {
                        Text(text = genre.name)
                    }
                }
            }
        }
        // episode title + actions
        item {
            TopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.podcast_episodes),
                    )
                },
                actions = {
                    // sort button
                    IconButton(onClick = { performAction(PodcastViewModel.Action.ToggleSortOrder) }) {
                        Icon(
                            painter = painterResource(
                                id = if (state.sortOrder == SortOrder.DESC)
                                    R.drawable.ic_sort_asc else R.drawable.ic_sort_desc
                            ),
                            contentDescription = null
                        )
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }

        // episodes
        items(lazyPagingItems = lazyPagingItems) { uiModel ->
            uiModel?.let {
                if (uiModel is EpisodeUiModel.EpisodeItem) {
                    PodcastEpisodeItem(
                        episode = uiModel.episode,
                        onEpisodeClick = {
                            performAction(PodcastViewModel.Action.OpenEpisodeDetail(uiModel.episode))
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
                                        onClick = {
                                            performAction(
                                                PodcastViewModel.Action.PlayNextEpisode(
                                                    uiModel.episode
                                                )
                                            )
                                        },
                                    ),
                                    BottomSheetMenuItem(
                                        titleId = R.string.action_play_last,
                                        icon = Icons.Default.AddToQueue,
                                        onClick = {
                                            performAction(
                                                PodcastViewModel.Action.PlayLastEpisode(
                                                    uiModel.episode
                                                )
                                            )
                                        },
                                    ),
                                    if (uiModel.episode.isSaved.not())
                                        BottomSheetMenuItem(
                                            titleId = R.string.action_save_episode,
                                            icon = Icons.Outlined.BookmarkAdd,
                                            onClick = {
                                                performAction(
                                                    PodcastViewModel.Action.ToggleSaveEpisode(
                                                        uiModel.episode
                                                    )
                                                )
                                            },
                                        )
                                    else
                                        BottomSheetMenuItem(
                                            titleId = R.string.action_remove_saved_episode,
                                            icon = Icons.Outlined.BookmarkRemove,
                                            onClick = {
                                                performAction(
                                                    PodcastViewModel.Action.ToggleSaveEpisode(
                                                        uiModel.episode
                                                    )
                                                )
                                            },
                                        ),
                                    BottomSheetSeparator,
                                    BottomSheetMenuItem(
                                        titleId = R.string.action_share_episode,
                                        icon = Icons.Default.Share,
                                        onClick = {
                                            performAction(
                                                PodcastViewModel.Action.ShareEpisode(
                                                    uiModel.episode
                                                )
                                            )
                                        },
                                    )
                                ),
                                drawerState = drawerState,
                                drawerContent = drawerContent
                            )
                        }
                    )
                    Divider()
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


@Composable
private fun PodcastHeader(
    modifier: Modifier,
    state: PodcastViewModel.State,
    lazyListState: LazyListState,
    openStoreDataDetail: (StoreData) -> Unit,
) {
    val podcastData = state.podcast
    val artistData = state.artistData

    val expandedAlpha = lazyListState.expandedHeaderAlpha
    val bgDominantColor = podcastData?.artworkDominantColor
        ?.let { Color.getColor(it) }
        ?: MaterialTheme.colors.onSurface
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    0.0f to bgDominantColor.copy(alpha = 0.5f),
                    0.2f to bgDominantColor.copy(alpha = 0.5f),
                    0.6f to Color.Transparent,
                    startY = 0.0f,
                    endY = Float.POSITIVE_INFINITY
                )
            ),
    ) {
        // Draw a scrim over the status bar which matches the app bar
        Spacer(
            Modifier
                //.background(appBarColor)
                .fillMaxWidth()
                .statusBarsHeight()
        )

        // spacer behind top app bar
        Spacer(Modifier.height(56.dp))

        // thumbnail
        Card(
            backgroundColor = Color.Transparent,
            shape = RoundedCornerShape(8.dp),
            elevation = 2.dp,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .align(Alignment.Start)
        ) {
            Image(
                painter = rememberCoilPainter(request = podcastData?.artworkUrl),
                contentDescription = podcastData?.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .alpha(expandedAlpha)
                    .placeholder(
                        visible = (podcastData == null),
                        shape = RoundedCornerShape(8.dp),
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // podcast
        Text(
            text = podcastData?.name.orEmpty(),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.Start)
                .placeholder(
                    visible = (podcastData == null),
                    highlight = PlaceholderHighlight.shimmer(),
                ),
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.h5,
        )

        // artist name + link
        val clickableArtistMod = artistData?.let {
            Modifier.clickable { openStoreDataDetail(it) }
        } ?: Modifier

        Text(
            text = with(AnnotatedString.Builder()) {
                append(podcastData?.artistName.orEmpty())
                artistData?.let {
                    append(" ›")
                }
                toAnnotatedString()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 4.dp)
                .then(clickableArtistMod)
                .placeholder(
                    visible = (podcastData == null),
                    highlight = PlaceholderHighlight.shimmer(),
                ),
            style = MaterialTheme.typography.body1,
            maxLines = 1,
            color = artistData?.let { MaterialTheme.colors.primary } ?: Color.Unspecified,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
private fun PodcastScreenTopAppBar(
    modifier: Modifier,
    state: PodcastViewModel.State,
    lazyListState: LazyListState,
    navigateUp: () -> Unit,
) {
    val appBarAlpha = lazyListState.topAppBarAlpha
    val backgroundColor: Color = Color.blendARGB(
        MaterialTheme.colors.surface.copy(alpha = 0f),
        MaterialTheme.colors.surface,
        appBarAlpha)

    // top app bar
    val artwork = state.podcast?.artworkDominantColor
    val contentEndColor = contentColorFor(MaterialTheme.colors.surface)

    Column(
        modifier = modifier
            .background(backgroundColor)
            .statusBarsPadding()
            .navigationBarsPadding(bottom = false)
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
                CompositionLocalProvider(LocalContentAlpha provides appBarAlpha) {
                    Text(text = state.podcast?.name.orEmpty())
                }
            },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = { },
            backgroundColor = Color.Transparent,
            contentColor = contentEndColor,
            elevation = 0.dp
        )
        if (appBarAlpha == 1f)
            Divider()
    }
}



@Composable
private fun PodcastDescriptionContent(description: String?) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .animateContentSize()
    ) {
        if (description != null) {
            OverflowHtmlText(
                text = description,
                overflow = TextOverflow.Clip,
                textAlign = TextAlign.Start,
                maxLines = 2
            )
        }
        else {
            Text(
                text = LoremIpsum(10).values.joinToString(" "),
                modifier = Modifier.placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                )
            )
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PodcastActionAppBar(
    state: PodcastViewModel.State,
    performAction: (PodcastViewModel.Action) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    // action buttons
    TopAppBar(
        modifier = Modifier,
        title = {
            // following button
            ActionChipButton(
                selected = (state.followingStatus == FollowStatus.FOLLOWED),
                onClick = {
                    if (state.followingStatus == FollowStatus.UNFOLLOWED)
                        performAction(PodcastViewModel.Action.Follow)
                    else if (state.followingStatus == FollowStatus.FOLLOWED)
                        performAction(PodcastViewModel.Action.Unfollow)
                },
                icon = {
                    Crossfade(targetState = state.followingStatus) { followStatus ->
                        when (followStatus) {
                            FollowStatus.FOLLOWING ->
                                LinearProgressIndicator(
                                    color = contentColorForExtended(MaterialTheme.colors.onSurface),
                                    modifier = Modifier
                                        .width(24.dp))
                            FollowStatus.UNFOLLOWED ->
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(id = R.string.action_follow),
                                )
                            FollowStatus.FOLLOWED ->
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(id = R.string.action_follow),
                                )
                        }
                    }
                }
            ) {
                Text(
                    text = when (state.followingStatus) {
                        FollowStatus.UNFOLLOWED -> stringResource(id = R.string.action_follow)
                        else -> stringResource(id = R.string.action_following)
                    }
                )
            }
        },
        actions = {
            if (state.followingStatus == FollowStatus.FOLLOWED) {
                // notification button
                IconButton(onClick = { performAction(PodcastViewModel.Action.ToggleNotifications) }) {
                    Icon(
                        imageVector = Icons.Default.NotificationsOff,
                        contentDescription = null
                    )
                }
                // settings button
                IconButton(onClick = { performAction(PodcastViewModel.Action.OpenSettings) }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null
                    )
                }
            }
            // more button
            Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(onClick = {
                        expanded = false
                        performAction(PodcastViewModel.Action.SharePodcast)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp)
                        )
                        Text(text = stringResource(id = R.string.action_share))
                    }
                    DropdownMenuItem(onClick = {
                        expanded = false
                        performAction(PodcastViewModel.Action.OpenWebsite)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(20.dp)
                        )
                        Text(text = stringResource(id = R.string.action_open_website))
                    }
                }
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}

@Composable
fun PodcastMetaData(podcastData: Podcast,
                    onCategoryClick: (Category) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val tintColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
        // rating
        podcastData.userRating?.let { rating ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    modifier = Modifier.size(16.dp),
                    contentDescription = null,
                    tint = tintColor
                )
                Text(
                    text = rating.toString(),
                    color = tintColor
                )
            }
        }
        // category
        podcastData.genre?.let { category ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.clickable(onClick = {
                    onCategoryClick(category)
                })
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    modifier = Modifier.size(16.dp),
                    contentDescription = null,
                    tint = tintColor
                )
                Text(
                    text = category.name,
                    color = tintColor
                )
            }
        }
    }
}

@Composable
@ReadOnlyComposable
fun contentColorForExtended(backgroundColor: Color): Color =
    when (backgroundColor) {
        MaterialTheme.colors.primary -> MaterialTheme.colors.onPrimary
        else -> {
            if (backgroundColor.luminance() > 0.5) Color.Black
            else Color.White
        }
    }


@Composable
fun PodcastEpisodesLoadingScreen() {
    LoadingListShimmer { list, floatAnim ->
        val brush = Brush.verticalGradient(list, 0f, floatAnim)
        Column(modifier = Modifier
            .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // header
            Spacer(modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(fraction = 0.6f)
                .height(25.dp)
                .background(brush = brush))

            repeat(6) {
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .background(brush = brush))
                    },
                    secondaryText = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(3) {
                                Spacer(modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .background(brush = brush))
                            }
                        }
                    },
                    icon = {
                        Spacer(modifier = Modifier
                            .size(40.dp)
                            .background(brush = brush))
                    }
                )
            }
        }
    }
}
