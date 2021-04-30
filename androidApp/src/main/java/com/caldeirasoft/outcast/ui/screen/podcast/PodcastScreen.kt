package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.common.Constants
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.nestedscrollview.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsBottomSheet
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataActions
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreDataScreenHeader
import com.caldeirasoft.outcast.ui.theme.*
import com.caldeirasoft.outcast.ui.util.toDp
import com.caldeirasoft.outcast.ui.util.toPx
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

val LocalVibrantColor = compositionLocalOf<Color> { error("No vibrant color") }

@OptIn(ExperimentalAnimationApi::class, FlowPreview::class, InternalCoroutinesApi::class)
@Composable
fun PodcastScreen(
    viewModel: PodcastViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val lazyPagingItems = viewModel.episodes.collectAsLazyPagingItems()
    PodcastScreen(
        state = state,
        drawerState = drawerState,
        lazyPagingItems = lazyPagingItems
    ) { action ->
        when (action) {
            is PodcastActions.NavigateUp -> navigateBack()
            is PodcastActions.OpenEpisodeDetail -> navigateTo(Screen.EpisodeScreen(action.episode, true))
            is PodcastActions.OpenStoreDataDetail -> navigateTo(Screen.StoreDataScreen(action.storeData))
            is PodcastActions.OpenCategoryDataDetail -> navigateTo(Screen.StoreDataScreen(action.category))
            else -> viewModel.submitAction(action)
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


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@ExperimentalCoroutinesApi
@Composable
private fun PodcastScreen(
    state: PodcastState,
    lazyPagingItems: LazyPagingItems<Episode>,
    drawerState: ModalBottomSheetState,
    actioner : (PodcastActions) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val podcastData = state.podcast
    val dominantColor = remember(state.podcast) { GetPodcastVibrantColor(podcastData = state.podcast) }
    val dominantColorOrDefault = dominantColor ?: MaterialTheme.colors.primary

    CompositionLocalProvider(LocalVibrantColor provides dominantColorOrDefault) {
        Scaffold {
            //
            val nestedScrollViewState = rememberNestedScrollViewState()

            VerticalNestedScrollView(
                state = nestedScrollViewState,
                header = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        PodcastExpandedHeader(
                            state = state,
                            nestedScrollViewState = nestedScrollViewState,
                            openStoreDataDetail = { actioner(PodcastActions.OpenStoreDataDetail(it)) }
                        )
                        // buttons
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.Center) {
                            val fullWidth = constraints.maxWidth
                            val buttonWidth = 150.dp.toPx()
                            val twoButtonsWidth = 2 * buttonWidth + 20.dp.toPx()
                            val edgePadding = (fullWidth - buttonWidth) / 2
                            val edgePadding2Btns = (fullWidth - twoButtonsWidth) / 2
                            // settings
                            FollowingButton(
                                textContent = stringResource(id = R.string.action_settings),
                                imageVector = Icons.Default.Settings,
                                translationValue = if (state.followingStatus == FollowStatus.FOLLOWED) edgePadding - edgePadding2Btns else 0f,
                                alphaValue = if (state.followingStatus == FollowStatus.FOLLOWED) 1f else 0f,
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.show()
                                    }
                                }
                            )

                            // following buttons
                            FollowingButton(
                                textContent = stringResource(id = R.string.action_following),
                                imageVector = Icons.Default.Check,
                                translationValue = if (state.followingStatus == FollowStatus.FOLLOWED) edgePadding2Btns - edgePadding else 0f,
                                alphaValue = if (state.followingStatus == FollowStatus.FOLLOWED) 1f else 0f,
                                onClick = { actioner(PodcastActions.UnfollowPodcast) }
                            )

                            // follow button
                            FollowButton(
                                state = state,
                                onClick = { actioner(PodcastActions.FollowPodcast) }
                            )
                        }
                    }
                },
                content = {
                    LazyListLayout(lazyListItems = lazyPagingItems) {
                        val listState = rememberLazyListState(0)
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                        ) {

                            /* description if present */
                            item {
                                podcastData.description?.let { description ->
                                    PodcastDescriptionContent(description = description)
                                }
                            }

                            // genre
                            item {
                                podcastData.category?.let { genre ->
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

                            // episodes
                            item {
                                StoreHeadingSection(
                                    title = stringResource(
                                        id = R.string.podcast_x_episodes,
                                        state.episodes.size
                                    )
                                )
                            }
                            if (state.showAllEpisodes || state.episodes.size < 5) {
                                items(items = state.episodes) { episode ->
                                    EpisodeItem(
                                        episode = episode,
                                        onEpisodeClick = {
                                            actioner(PodcastActions.OpenEpisodeDetail(episode))
                                        }
                                    )
                                    Divider()
                                }
                            } else {
                                // show first, last and "show more" button
                                item {
                                    // most recent episode
                                    state.episodes[0].let { firstEpisode ->
                                        EpisodeItem(
                                            episode = firstEpisode,
                                            onEpisodeClick = {
                                                actioner(
                                                    PodcastActions.OpenEpisodeDetail(
                                                        firstEpisode
                                                    )
                                                )
                                            }
                                        )
                                        Divider()
                                    }
                                }

                                item {
                                    // text button "show more episodes"
                                    TextButton(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp),
                                        colors = ButtonDefaults.textButtonColors(
                                            contentColor = LocalVibrantColor.current
                                        ),
                                        onClick = { actioner(PodcastActions.ShowAllEpisodes) })
                                    {
                                        Text(
                                            text = stringResource(id = R.string.action_show_all_episodes),
                                            style = typography.button.copy(letterSpacing = 0.25.sp)
                                        )
                                    }
                                    Divider()
                                }

                                item {
                                    // oldest episode
                                    val size = state.episodes.size - 1
                                    state.episodes[size].let { lastEpisode ->
                                        EpisodeItem(
                                            episode = lastEpisode,
                                            onEpisodeClick = {
                                                actioner(
                                                    PodcastActions.OpenEpisodeDetail(
                                                        lastEpisode
                                                    )
                                                )
                                            }
                                        )
                                        Divider()
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
            )

            PodcastTopAppBar(
                state = state,
                nestedScrollViewState = nestedScrollViewState,
                navigateUp = { actioner(PodcastActions.NavigateUp) }
            )
        }
    }
}

@Composable
private fun PodcastExpandedHeader(
    state: PodcastState,
    nestedScrollViewState: NestedScrollViewState,
    openStoreDataDetail: (StoreData) -> Unit,
) {
    val podcastData = state.podcast
    val artistData = state.artistData
    val dominantColor =
        podcastData.artworkDominantColor
            ?.let { Color.getColor(it).takeUnless { color -> color == Color.White } }

    val alphaLargeHeader = nestedScrollViewState.expandedHeaderAlpha
    Box(modifier = Modifier.fillMaxWidth())
    {
        val bgDominantColor =
                Color.getColor(podcastData.artworkDominantColor) ?: MaterialTheme.colors.surface
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        0.0f to bgDominantColor.copy(alpha = 0.5f),
                        0.2f to bgDominantColor.copy(alpha = 0.5f),
                        0.6f to Color.Transparent,
                        startY = 0.0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
                .padding(horizontal = 16.dp)
                .padding(top = 56.dp, bottom = 8.dp)
                .alpha(alphaLargeHeader),
        ) {
            // status bar spacer
            Spacer(modifier = Modifier.statusBarsHeight())

            // thumbnail
            Card(
                backgroundColor = Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                elevation = 2.dp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = rememberCoilPainter(request = podcastData.artworkUrl),
                    contentDescription = podcastData.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(200.dp)
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // podcast
            Text(
                text = podcastData.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
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
                    .padding(bottom = 4.dp)
                    .then(clickableArtistMod),
                style = MaterialTheme.typography.body1,
                maxLines = 2,
                color = artistData?.let { dominantColor /*MaterialTheme.colors.primary*/ }
                    ?: Color.Unspecified,
                textAlign = TextAlign.Center
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
                    Text(text = state.podcast.name)
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

@Composable
fun PodcastLoadingScreen() {
    LoadingListShimmer { list, floatAnim ->
        val brush = Brush.verticalGradient(list, 0f, floatAnim)
        Column(modifier = Modifier
            .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
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

            // episodes
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
}

@Composable
fun FollowingButton(
    textContent: String,
    imageVector: ImageVector,
    translationValue: Float,
    alphaValue: Float,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = Modifier
            .width(150.dp)
            .graphicsLayer(
                translationX = animateFloatAsState(
                    targetValue = translationValue,
                    animationSpec = tween(durationMillis = 750)
                ).value,
                alpha = animateFloatAsState(
                    targetValue = alphaValue,
                    animationSpec = tween(durationMillis = 750)
                ).value
            ),
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = LocalVibrantColor.current
        ),
        contentPadding = PaddingValues(start = 24.dp,
            end = 24.dp,
            top = 8.dp,
            bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = imageVector,
                contentDescription = textContent,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(text = textContent,
                style = typography.button.copy(letterSpacing = 0.5.sp))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FollowButton(state: PodcastState, onClick: () -> Unit) {
    AnimatedVisibility(
        visible = (state.followingStatus != FollowStatus.FOLLOWED),
        enter = fadeIn(animationSpec = tween(durationMillis = 250)),
        exit = fadeOut(animationSpec = tween(durationMillis = 250)))
    {
        Button(
            modifier = Modifier
                .width(150.dp),
            onClick = {
                if (state.followingStatus == FollowStatus.UNFOLLOWED)
                    onClick()
            },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = LocalVibrantColor.current,
                contentColor = contentColorForExtended(LocalVibrantColor.current)
            ),
            contentPadding = PaddingValues(start = 24.dp,
                end = 24.dp,
                top = 8.dp,
                bottom = 8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Crossfade(targetState = state.followingStatus) { followStatus ->
                    when (followStatus) {
                        FollowStatus.FOLLOWING ->
                            LinearProgressIndicator(
                                color = contentColorForExtended(LocalVibrantColor.current),
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 4.dp))
                        FollowStatus.UNFOLLOWED ->
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(id = R.string.action_follow),
                                modifier = Modifier.padding(end = 4.dp)
                            )
                    }
                }
                Text(text = stringResource(id = R.string.action_follow),
                    style = typography.button.copy(letterSpacing = 0.5.sp))
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
