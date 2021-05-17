package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episodes.EpisodeUiModel
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.theme.*
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import timber.log.Timber

@OptIn(ExperimentalAnimationApi::class, FlowPreview::class, InternalCoroutinesApi::class)
@Composable
fun PodcastScreen(
    viewModel: PodcastViewModel,
    storePodcast: StorePodcast? = null,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val lazyPagingItems = viewModel.episodes.collectAsLazyPagingItems()

    storePodcast?.let {
        LaunchedEffect(storePodcast) {
            viewModel.setPodcast(it)
        }
    }

    PodcastScreen(
        state = state,
        scaffoldState = scaffoldState,
        lazyPagingItems = lazyPagingItems,
        navigateBack = navigateBack,
        navigateTo = navigateTo,
        onFollowPodcast = viewModel::follow,
        onUnfollowPodcast = viewModel::unfollow,
        onMoreButtonClick = {

        },
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
                    BottomSheetMenuItem(
                        titleId = R.string.action_save_episode,
                        icon = Icons.Default.FavoriteBorder,
                        onClick = { viewModel.saveEpisode(episode) },
                    ),
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

    ObserveUiEffects(
        viewModel = viewModel,
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@ExperimentalCoroutinesApi
@Composable
private fun PodcastScreen(
    state: PodcastState,
    scaffoldState: ScaffoldState,
    lazyPagingItems: LazyPagingItems<EpisodeUiModel>,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
    onFollowPodcast: () -> Unit,
    onUnfollowPodcast: () -> Unit,
    onMoreButtonClick: () -> Unit,
    onEpisodeItemMoreButtonClick: (Episode) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val podcastData = state.podcast
    Scaffold(
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
    ) {
        //
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight
            val headerRatio: Float = 1 / 2f
            val headerHeight = remember { mutableStateOf((screenHeight * headerRatio).toInt()) }
            var expandedAlpha by remember { mutableStateOf(1f) }

            val collapsingToolbarState = rememberCollapsingToolbarState()

            AppbarContainer(
                modifier = Modifier.fillMaxWidth(),
                scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
                collapsingToolbarState = collapsingToolbarState
            ) {
                CollapsingToolbar(collapsingToolbarState = collapsingToolbarState) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = headerHeight.value.toDp())
                            .pin()
                    ) {
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = headerHeight.value.toDp())
                            .parallax()
                            .progress {
                                expandedAlpha = it
                            }
                            .alpha(expandedAlpha)
                    ) {
                        if ((podcastData != null) && (!state.isLoading)) {
                            // header
                            PodcastHeader(
                                modifier = Modifier
                                    .height(headerHeight.value.toDp()),
                                state = state,
                                collapsingToolbarState = collapsingToolbarState,
                                openStoreDataDetail = {
                                    navigateTo(Screen.StoreDataScreen(it))
                                }
                            )
                        }
                        else {
                            PodcastHeaderLoadingScreen(headerHeight.value.toDp())
                        }
                    }

                    PodcastScreenTopAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .navigationBarsPadding(bottom = false)
                            .pin(),
                        state = state,
                        collapsingToolbarState = collapsingToolbarState,
                        navigateUp = navigateBack
                    )
                }
                val listState = lazyPagingItems.rememberLazyListStateWithPagingItems()
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .appBarBody()
                ) {
                    // action buttons
                    item {
                        podcastData?.let {
                            PodcastActionButtons(
                                state = state,
                                onFollowPodcast = onFollowPodcast,
                                onUnfollowPodcast = onUnfollowPodcast,
                                onOpenPodcastContextMenu = onMoreButtonClick
                            )
                        }
                    }
                    // description
                    item {
                        podcastData?.description?.let { description ->
                            PodcastDescriptionContent(description = description)
                        }
                    }
                    // genre
                    item {
                        podcastData?.category?.let { genre ->
                            Box(modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 8.dp)
                            ) {
                                ChipButton(selected = false,
                                    onClick = {
                                        navigateTo(Screen.StoreDataScreen(genre))
                                    })
                                {
                                    Text(text = genre.name)
                                }
                            }
                        }
                    }
                    // episode title
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                stringResource(id = R.string.podcast_episodes),
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                style = MaterialTheme.typography.h6
                            )

                            ActionChipButton(
                                modifier = Modifier.align(Alignment.CenterVertically),
                                onClick = { /*TODO*/ },
                                icon = {
                                    Icon(imageVector = Icons.Filled.Sort,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 4.dp)
                                            .size(16.dp)
                                            .align(Alignment.CenterVertically))
                                }) {
                                Text(text = stringResource(id = R.string.action_filter))
                            }
                        }
                    }

                    // episodes
                    items(lazyPagingItems = lazyPagingItems) { uiModel ->
                        uiModel?.let {
                            when(uiModel) {
                                is EpisodeUiModel.EpisodeItem -> {
                                    PodcastEpisodeItem(
                                        episode = uiModel.episode,
                                        onEpisodeClick = { navigateTo(Screen.EpisodeScreen(uiModel.episode)) },
                                        onContextMenuClick = { onEpisodeItemMoreButtonClick(uiModel.episode) }
                                    )
                                    Divider()
                                }
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
private fun PodcastHeader(
    modifier: Modifier,
    state: PodcastState,
    collapsingToolbarState: CollapsingToolbarState,
    openStoreDataDetail: (StoreData) -> Unit,
) {
    val podcastData = state.podcast
    val artistData = state.artistData

    val expandedAlpha = collapsingToolbarState.expandedAlpha
    podcastData?.let {
        val bgDominantColor = podcastData.artworkDominantColor
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
            val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)

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
                    painter = rememberCoilPainter(request = podcastData.artworkUrl),
                    contentDescription = podcastData.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // podcast
            Text(
                text = podcastData.name,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Start),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.h5,
            )

            // artist name + link
            val clickableArtistMod = artistData?.let {
                Modifier.clickable { openStoreDataDetail(it) }
            } ?: Modifier

            Text(
                text = with(AnnotatedString.Builder()) {
                    append(podcastData.artistName)
                    artistData?.let {
                        append(" ›")
                    }
                    toAnnotatedString()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 4.dp)
                    .then(clickableArtistMod),
                style = MaterialTheme.typography.body1,
                maxLines = 1,
                color = artistData?.let { MaterialTheme.colors.primary } ?: Color.Unspecified,
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
private fun PodcastScreenTopAppBar(
    modifier: Modifier,
    state: PodcastState,
    collapsingToolbarState: CollapsingToolbarState,
    navigateUp: () -> Unit,
) {
    Timber.d("progress: ${collapsingToolbarState.progress}, alpha:${collapsingToolbarState.collapsedAlpha}")
    val appBarAlpha = collapsingToolbarState.collapsedAlpha
    val backgroundColor: Color = Color.blendARGB(
        MaterialTheme.colors.surface.copy(alpha = 0f),
        MaterialTheme.colors.surface,
        appBarAlpha)

    // top app bar
    val artwork = state.podcast?.artworkDominantColor
    val contentEndColor = contentColorFor(MaterialTheme.colors.surface)

    Column(
        modifier = modifier
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
        if (collapsingToolbarState.progress == 0f)
            Divider()
    }
}



@Composable
private fun PodcastDescriptionContent(description: String) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .animateContentSize()
    ) {
        OverflowHtmlText(text = description,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Start,
            maxLines = 2)
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PodcastActionButtons(
    state: PodcastState,
    onFollowPodcast: () -> Unit,
    onUnfollowPodcast: () -> Unit,
    onOpenPodcastContextMenu: () -> Unit,
) {
    // action buttons
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(36.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // following button
        ActionChipButton(
            selected = (state.followingStatus == FollowStatus.FOLLOWED),
            onClick = {
                if (state.followingStatus == FollowStatus.UNFOLLOWED)
                    onFollowPodcast()
                else if (state.followingStatus == FollowStatus.FOLLOWED)
                    onUnfollowPodcast()
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

        // more button
        IconButton(onClick = onOpenPodcastContextMenu) {
            Icon(imageVector = Icons.Default.MoreHoriz,
                contentDescription = null)
        }
    }
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
        podcastData.category?.let { category ->
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

fun GetPodcastVibrantColor(podcastData: Podcast?): Color? =
    podcastData
        ?.artworkDominantColor
        ?.let { Color.getColor(it).takeUnless { it == Color.White || it == Color.Black } }
        ?.let { color ->
            Timber.d(String.format("color : #%06X", (0xFFFFFF and color.toArgb())))
            if (color.luminance() > 0.7)
                color.toxyY().let { xyYtoColor(it[0], it[1], 0.5f) }
                    .also {
                        Timber.d(String.format("Dim color : #%06X", (0xFFFFFF and it.toArgb())))
                    }
            else color
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
fun PodcastHeaderLoadingScreen(headerHeightDp: Dp) {
    LoadingListShimmer { list, floatAnim ->
        val brush = Brush.verticalGradient(list, 0f, floatAnim)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 3.dp)
        ) {
            // Header
            Column(modifier = Modifier.height(headerHeightDp)) {

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
                    Spacer(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .background(brush = brush)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // podcast
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 3.dp)
                        .height(18.dp)
                        .background(brush = brush)
                )

                // artist name
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 3.dp)
                        .height(14.dp)
                        .background(brush = brush)
                )

                // rating / category
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 3.dp)
                        .height(14.dp)
                        .background(brush = brush)
                )
            }

            // Buttons
            Surface(
                modifier = Modifier
                    .size(height = 35.dp, width = 100.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(50)
            ) {
                Spacer(modifier = Modifier
                    .fillMaxSize()
                    .background(brush = brush))
            }

            // description
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(3) {
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(brush = brush))
                }
            }
        }
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

@Composable
private fun ObserveUiEffects(
    viewModel: PodcastViewModel,
    state: PodcastState,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
            }
        }
    }
}
