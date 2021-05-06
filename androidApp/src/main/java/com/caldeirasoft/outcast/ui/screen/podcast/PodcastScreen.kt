package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.nestedscrollview.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsBottomSheet
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.theme.*
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
    PodcastScreen(
        state = state,
        scaffoldState = scaffoldState,
        drawerState = drawerState,
        lazyPagingItems = lazyPagingItems
    ) { action ->
        when (action) {
            is PodcastActions.NavigateUp -> navigateBack()
            is PodcastActions.OpenEpisodeDetail -> navigateTo(Screen.EpisodeScreen(action.episode.feedUrl, action.episode.guid, true))
            is PodcastActions.OpenStoreDataDetail -> navigateTo(Screen.StoreDataScreen(action.storeData))
            is PodcastActions.OpenCategoryDataDetail -> navigateTo(Screen.StoreDataScreen(action.category))
            is PodcastActions.OpenPodcastContextMenu -> {
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Open podcast context menu")
                    //drawerState.show()
                }
            }
            is PodcastActions.OpenSettings -> {
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar("Open podcast settings")
                    //drawerState.show()
                }
            }
            else -> viewModel.submitAction(action)
        }
    }

    LaunchedEffect(viewModel) {
        storePodcast?.let {
            viewModel.submitAction(PodcastActions.SetPodcast(it))
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
            }
        }
    }

    LaunchedEffect(viewModel) {
        drawerContent.updateContent {
            PodcastSettingsBottomSheet(
                state = state,
                dataStore = viewModel.dataStore
            ) { action ->
                when (action) {
                    is PodcastActions.NavigateUp -> coroutineScope.launch { drawerState.hide() }
                    else -> viewModel.submitAction(action)
                }
            }
        }
    }
}

/**
 * This is the minimum amount of calculated constrast for a color to be used on top of the
 * surface color. These values are defined within the WCAG AA guidelines, and we use a value of
 * 3:1 which is the minimum for user-interface components.
 */
private const val MinConstastOfPrimaryVsSurface = 3f

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@ExperimentalCoroutinesApi
@Composable
private fun PodcastScreen(
    state: PodcastState,
    scaffoldState: ScaffoldState,
    lazyPagingItems: LazyPagingItems<Episode>,
    drawerState: ModalBottomSheetState,
    actioner : (PodcastActions) -> Unit,
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

            val nestedScrollViewState = rememberNestedScrollViewState()
            VerticalNestedScrollView(
                state = nestedScrollViewState,
                header = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if ((podcastData != null) && (!state.isLoading)) {
                            // header
                            PodcastHeader(
                                modifier = Modifier.height(headerHeight.value.toDp()),
                                state = state,
                                nestedScrollViewState = nestedScrollViewState,
                                openStoreDataDetail = {
                                    actioner(
                                        PodcastActions.OpenStoreDataDetail(it)
                                    )
                                }
                            )
                            // Rating
                            PodcastMetaData(
                                podcastData = podcastData,
                                onCategoryClick = {
                                    actioner(
                                        PodcastActions.OpenCategoryDataDetail(it)
                                    )
                                }
                            )
                            // action buttons
                            PodcastActionButtons(
                                state = state,
                                onFollowPodcast = { actioner(PodcastActions.FollowPodcast) },
                                onUnfollowPodcast = { actioner(PodcastActions.UnfollowPodcast) },
                                onOpenPodcastContextMenu = { actioner(PodcastActions.OpenPodcastContextMenu) }
                            )
                            // description
                            podcastData.description?.let { description ->
                                PodcastDescriptionContent(description = description)
                            }
                        } else {
                            PodcastHeaderLoadingScreen(headerHeight.value.toDp())
                        }
                    }
                },
                content = {
                    LazyListLayout(
                        lazyListItems = lazyPagingItems,
                        onLoading = { PodcastEpisodesLoadingScreen() }
                    )
                    {
                        val listState = rememberLazyListState(0)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            /*
                                // genre
                                item {
                                    podcastData?.category?.let { genre ->
                                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                            ChipButton(selected = false,
                                                onClick = {
                                                    actioner(
                                                        PodcastActions.OpenCategoryDataDetail(
                                                            genre
                                                        )
                                                    )
                                                })
                                            {
                                                Text(text = genre.name)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                                */
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
                            items(lazyPagingItems = lazyPagingItems) { episode ->
                                episode?.let {
                                    PodcastEpisodeItem(
                                        episode = episode,
                                        onEpisodeClick = {
                                            actioner(
                                                PodcastActions.OpenEpisodeDetail(episode)
                                            )
                                        },
                                        onContextMenuClick = {}
                                    )
                                }
                                Divider()
                            }

                            item {
                                // bottom app bar spacer
                                Spacer(modifier = Modifier.height(56.dp))
                            }
                        }
                    }
                })

            PodcastTopAppBar(
                state = state,
                nestedScrollViewState = nestedScrollViewState,
                navigateUp = { actioner(PodcastActions.NavigateUp) }
            )
        }
    }
}

@Composable
private fun PodcastHeader(
    modifier: Modifier,
    state: PodcastState,
    nestedScrollViewState: NestedScrollViewState,
    openStoreDataDetail: (StoreData) -> Unit,
) {
    val podcastData = state.podcast
    val artistData = state.artistData

    val alphaLargeHeader = nestedScrollViewState.expandedHeaderAlpha
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
                )
                .alpha(alphaLargeHeader),
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
fun PodcastTopAppBar(
    state: PodcastState,
    nestedScrollViewState: NestedScrollViewState,
    navigateUp: () -> Unit,
) {
    val appBarAlpha = nestedScrollViewState.topAppBarAlpha
    val backgroundColor: Color = Color.blendARGB(
        MaterialTheme.colors.surface.copy(alpha = 0f),
        MaterialTheme.colors.surface,
        appBarAlpha)

    // top app bar
    val contentColor = contentColorFor(MaterialTheme.colors.surface)
    /*
    val artwork = state.podcast.artworkDominantColor
    val contentEndColor = contentColorFor(MaterialTheme.colors.surface)
    val contentColor: Color =
        artwork?.textColor1
            ?.let {
                val contentStartColor = Color.getColor(it)
                Color.blendARGB(contentStartColor,
                    contentEndColor,
                    appBarAlpha)
            }
            ?: contentEndColor*/

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .fillMaxWidth()
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
            contentColor = contentColor,
            elevation = 0.dp
        )
        Divider(modifier = Modifier.alpha(appBarAlpha))
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
            maxLines = 3)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
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
    /*
        // episode count
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Tag,
                modifier = Modifier.size(16.dp),
                contentDescription = null,
                tint = tintColor
            )
            Text(
                text = stringResource(id = R.string.podcast_x_episodes, podcastData.trackCount),
                color = tintColor
            )
        }
         */

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
                        .height(14.dp)
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
                    .size(height = 25.dp, width = 90.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 3.dp),
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

