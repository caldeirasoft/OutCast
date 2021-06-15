package com.caldeirasoft.outcast.ui.screen.episode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.screen.podcast.GetPodcastVibrantColor
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.util.navigateToPodcast
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import cz.levinzonr.router.core.Route
import cz.levinzonr.router.core.RouteArg
import cz.levinzonr.router.core.RouteArgType
import kotlinx.coroutines.flow.collect

@Route(
    name = "episode",
    args = [
        RouteArg("feedUrl", RouteArgType.StringType, false),
        RouteArg("guid", RouteArgType.StringType, false)
    ]
)
@Composable
fun EpisodeScreen(
    viewModel: EpisodeViewModel,
    storeEpisode: StoreEpisode? = null,
    fromSamePodcast: Boolean = false,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    EpisodeScreen(
        state = state,
        scaffoldState = scaffoldState,
        onPodcastClick = viewModel::openPodcastDetails,
        navigateUp = { navController.navigateUp() },
        onSaveButtonClick = viewModel::toggleSaveEpisode,
        onMoreButtonClick = { episode ->
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
                        onClick = { viewModel.playNext() },
                    ),
                    BottomSheetMenuItem(
                        titleId = R.string.action_play_last,
                        icon = Icons.Default.AddToQueue,
                        onClick = { viewModel.playLast() },
                    ),
                    BottomSheetSeparator,
                    BottomSheetMenuItem(
                        titleId = R.string.action_share_episode,
                        icon = Icons.Default.Share,
                        onClick = { viewModel.shareEpisode() },
                    )
                ),
                drawerState = drawerState,
                drawerContent = drawerContent
            )
        }
    )

    storeEpisode?.let {
        LaunchedEffect(storeEpisode) {
            viewModel.setEpisode(it)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when(event) {
                is EpisodeEvent.OpenPodcastDetail -> {
                    if (fromSamePodcast) navController.navigateUp()
                    else navController.navigateToPodcast(event.podcast)
                }
                is EpisodeEvent.PlayEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Play episode")
                is EpisodeEvent.PlayNextEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Play next episode")
                is EpisodeEvent.PlayLastEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Play last episode")
                is EpisodeEvent.DownloadEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Download episode")
                is EpisodeEvent.RemoveDownloadEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Remove download episode")
                is EpisodeEvent.CancelDownloadEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Cancel download episode")
                is EpisodeEvent.SaveEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Save episode")
                is EpisodeEvent.RemoveFromSavedEpisodesEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Remove from saved episode")
                is EpisodeEvent.ShareEpisodeEvent ->
                    scaffoldState.snackbarHostState.showSnackbar("Share episode")
            }
        }
    }
}

@Composable
fun EpisodeScreen(
    state: EpisodeViewState,
    scaffoldState: ScaffoldState,
    navigateUp: () -> Unit,
    onPodcastClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    onMoreButtonClick: (Episode) -> Unit,
) {
    val dominantColor = remember(state.podcast) { GetPodcastVibrantColor(podcastData = state.podcast) }
    val dominantColorOrDefault = dominantColor ?: MaterialTheme.colors.primary

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
        val listState = rememberLazyListState(0)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            // header
            item {
                EpisodeExpandedHeader(
                    state = state,
                    listState = listState,
                    onPodcastClick = onPodcastClick,
                )
            }

            when {
                state.isLoading ->
                    item {
                        //PodcastLoadingScreen()
                    }
                state.error != null ->
                    state.error.let {
                        item {
                            ErrorScreen(t = it)
                        }
                    }
                else -> {
                    // buttons
                    item {
                        state.episode?.let {
                            EpisodeActionAppBar(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp),
                                episode = it,
                                onSaveButtonClick = onSaveButtonClick,
                                onContextMenuClick = {
                                    onMoreButtonClick(it)
                                }
                            )
                        }
                    }

                    /* description if present */
                    item {
                        state.episode?.description?.let { description ->
                            EpisodeDescriptionContent(description = description)
                        }
                    }

                    // custom artwork

                    item {
                        // bottom app bar spacer
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
            }
        }

        EpisodeTopAppBar(
            state = state,
            listState = listState,
            navigateUp = navigateUp,
        )
    }
}

@Composable
private fun EpisodeExpandedHeader(
    state: EpisodeViewState,
    listState: LazyListState,
    onPodcastClick: () -> Unit,
) {
    val dominantColor = MaterialTheme.colors.primary

    val alphaLargeHeader = listState.expandedHeaderAlpha
    Box(modifier = Modifier
        .fillMaxWidth()
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val bgDominantColor = dominantColor
            val bgDominantColorAnimated = animateColorAsState(targetValue = bgDominantColor).value
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    //.background(Color.Magenta.copy(alpha = 0.9f))
                    .background(
                        brush = Brush.verticalGradient(
                            0.0f to bgDominantColorAnimated.copy(alpha = 0.5f),
                            0.2f to bgDominantColorAnimated.copy(alpha = 0.5f),
                            0.6f to Color.Transparent,
                            startY = 0.0f,
                            endY = Float.POSITIVE_INFINITY
                        )
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
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
                    .align(Alignment.Start)
            ) {
                Image(
                    painter = rememberCoilPainter(request = state.episode?.artworkUrl.orEmpty()),
                    contentDescription = state.episode?.podcastName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // episode name
            Box(modifier = Modifier.heightIn(min = 50.dp)) {
                Text(
                    text = state.episode?.name.orEmpty(),
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // podcast name
            Text(
                text = with(AnnotatedString.Builder()) {
                    append(state.episode?.podcastName.orEmpty())
                    append(" ›")
                    toAnnotatedString()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .clickable(onClick = onPodcastClick),
                style = MaterialTheme.typography.body1,
                maxLines = 2,
                color = dominantColor
            )
            // date
        }
    }
}

@Composable
fun EpisodeTopAppBar(
    state: EpisodeViewState,
    listState: LazyListState,
    navigateUp: () -> Unit,
) {
    val appBarAlpha = listState.topAppBarAlpha
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
                    Text(text = state.episode?.name.orEmpty())
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
private fun EpisodeDescriptionContent(description: String) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .animateContentSize()
    ) {
        OverflowHtmlText(text = description,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Start,
            maxLines = 5)
    }
}

@Composable
private fun EpisodeActionAppBar(
    modifier: Modifier = Modifier,
    episode: Episode,
    onSaveButtonClick: () -> Unit,
    onContextMenuClick: () -> Unit,
) {
    val tintColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    TopAppBar(
        modifier = Modifier,
        title = {
            PlayButton(
                episode = episode
            )
        },
        actions = {
            // queued button
            // save button
            IconButton(onClick = onSaveButtonClick) {
                Icon(
                    imageVector = if (episode.isSaved) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                    contentDescription = null,
                    tint = tintColor,
                )
            }
            // more button
            IconButton(onClick = onContextMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = tintColor,
                )
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}
