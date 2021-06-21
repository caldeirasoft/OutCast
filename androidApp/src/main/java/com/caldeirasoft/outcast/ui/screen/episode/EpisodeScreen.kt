package com.caldeirasoft.outcast.ui.screen.episode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.BookmarkRemove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.util.navigateToPodcast
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

    EpisodeScreen(
        state = state,
        scaffoldState = scaffoldState,
        episode = state.episode,
        onPodcastClick = viewModel::openPodcastDetails,
        navigateUp = { navController.navigateUp() },
        onSaveButtonClick = viewModel::toggleSaveEpisode,
        contextMenuItems = listOf(
            ContextMenuItem(
                titleId = R.string.action_play_next,
                icon = Icons.Default.QueuePlayNext,
                onClickAction = { viewModel.playNext() },
            ),
            ContextMenuItem(
                titleId = R.string.action_play_last,
                icon = Icons.Default.AddToQueue,
                onClickAction = { viewModel.playLast() },
            ),
            ContextMenuSeparator,
            if (state.episode?.isSaved == false)
                ContextMenuItem(
                    titleId = R.string.action_save_episode,
                    icon = Icons.Outlined.BookmarkAdd,
                    onClickAction = { /*viewModel.saveEpisode(episode)*/ },
                )
            else
                ContextMenuItem(
                    titleId = R.string.action_remove_saved_episode,
                    icon = Icons.Outlined.BookmarkRemove,
                    onClickAction = { /*viewModel.removeSavedEpisode(episode)*/ },
                )
            ,
            ContextMenuSeparator,
            ContextMenuItem(
                titleId = R.string.action_share_episode,
                icon = Icons.Default.Share,
                onClickAction = { viewModel.shareEpisode() },
            )
        )
    )

    storeEpisode?.let {
        LaunchedEffect(storeEpisode) {
            viewModel.setEpisode(it)
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
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
    episode: Episode?,
    navigateUp: () -> Unit,
    onPodcastClick: () -> Unit,
    onSaveButtonClick: () -> Unit,
    contextMenuItems: List<BaseContextMenuItem>,
) {
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
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // header
            EpisodeExpandedHeader(
                episode = episode,
                listState = listState,
                onPodcastClick = onPodcastClick,
            )


            // action bar
            episode?.let {
                EpisodeActionAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    episode = it,
                    onSaveButtonClick = onSaveButtonClick,
                    contextMenuItems = contextMenuItems
                )
            }

            /* description if present */
            EpisodeDescriptionContent(description = episode?.description)

            // custom artwork
            // bottom app bar spacer
            Spacer(modifier = Modifier.height(56.dp))
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
    episode: Episode?,
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
                    painter = rememberCoilPainter(request = episode?.artworkUrl.orEmpty()),
                    contentDescription = episode?.podcastName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(120.dp)
                        .aspectRatio(1f)
                        .placeholder(
                            visible = (episode == null),
                            shape = RoundedCornerShape(8.dp),
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // episode name
            Box(modifier = Modifier.heightIn(min = 50.dp)) {
                if (episode != null) {
                    Text(
                        text = episode.name,
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                    )
                }
                else {
                    Text(
                        text = LoremIpsum(8).values.joinToString(" "),
                        style = MaterialTheme.typography.h5,
                        maxLines = 1,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .placeholder(
                                visible = true,
                                highlight = PlaceholderHighlight.shimmer(),
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // podcast name
            Text(
                text = with(AnnotatedString.Builder()) {
                    append(episode?.podcastName.orEmpty())
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
            //TODO: date
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
private fun EpisodeDescriptionContent(description: String?) {
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
                maxLines = 5
            )
        }
        else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(4) {
                    Text(
                        text = LoremIpsum(8).values.joinToString(" "),
                        maxLines = 1,
                        modifier = Modifier.placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun EpisodeActionAppBar(
    modifier: Modifier = Modifier,
    episode: Episode,
    onSaveButtonClick: () -> Unit,
    contextMenuItems: List<BaseContextMenuItem> = emptyList(),
) {
    val tintColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    var expandedContextMenu by remember { mutableStateOf(false) }
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
            Box(Modifier.wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { expandedContextMenu = !expandedContextMenu }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                        tint = tintColor,
                    )
                }
                DropdownMenu(
                    modifier = Modifier.widthIn(min = 250.dp),
                    expanded = expandedContextMenu,
                    onDismissRequest = { expandedContextMenu = false }
                ) {
                    contextMenuItems.forEach { value ->
                        when(value) {
                            is ContextMenuItem ->
                                DropdownMenuItem(
                                    modifier = Modifier,
                                    onClick = {
                                        expandedContextMenu = false
                                        value.onClickAction()
                                    }
                                ) {
                                    Icon(
                                        imageVector = value.icon,
                                        contentDescription = stringResource(id = value.titleId),
                                        modifier = Modifier
                                            .padding(end = 8.dp)
                                            .size(20.dp)
                                    )
                                    Text(text = stringResource(id = value.titleId))
                                }
                            is ContextMenuSeparator ->
                                Divider()
                        }
                    }
                }
            }
        },
        backgroundColor = Color.Transparent,
        elevation = 0.dp
    )
}
