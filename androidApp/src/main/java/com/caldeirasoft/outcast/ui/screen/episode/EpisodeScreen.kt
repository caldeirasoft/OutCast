package com.caldeirasoft.outcast.ui.screen.episode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.screen.base.Screen
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.DateFormatter.formatRelativeDateTime
import com.caldeirasoft.outcast.ui.util.DurationFormatter.formatDuration
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
    val scaffoldState = rememberScaffoldState()
    Screen(
        viewModel = viewModel,
        onEvent = { event ->
            when (event) {
                is EpisodeViewModel.Event.OpenPodcastDetail -> {
                    if (fromSamePodcast) navController.navigateUp()
                    else navController.navigateToPodcast(event.podcast)
                }
                is EpisodeViewModel.Event.Exit ->
                    navController.navigateUp()
                is EpisodeViewModel.Event.ShareEpisode ->
                    //Toast.makeText()
                    scaffoldState.snackbarHostState.showSnackbar("Share episode")
            }
        }
    ) { state, performAction ->
        EpisodeScreen(
            state = state,
            scaffoldState = scaffoldState,
            episode = state.episode,
            performAction = performAction,
        )
    }

    storeEpisode?.let {
        LaunchedEffect(storeEpisode) {
            viewModel.performAction(EpisodeViewModel.Action.SetEpisode(it))
        }
    }
}

@Composable
fun EpisodeScreen(
    state: EpisodeViewModel.State,
    scaffoldState: ScaffoldState,
    episode: Episode?,
    performAction: (EpisodeViewModel.Action) -> Unit
) {
    val listState = rememberLazyListState()
    ScaffoldWithLargeHeaderAndLazyColumn(
        headerRatio = 1 / 2f,
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
            EpisodeTopAppBar(
                state = state,
                listState = listState,
                navigateUp = { performAction(EpisodeViewModel.Action.Exit) }
            )
        },
        headerContent = {
            EpisodeHeader(
                episode = episode,
                podcastData = state.podcast,
                listState = listState,
                onPodcastClick = { performAction(EpisodeViewModel.Action.OpenPodcastDetail) },
            )
        }
    ) {
        // action bar
        episode?.let {
            item {
                EpisodeActionAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    episode = it,
                    onSaveButtonClick = { performAction(EpisodeViewModel.Action.ToggleSaveEpisode) },
                    contextMenuItems = listOf(
                        ContextMenuItem(
                            titleId = R.string.action_play_next,
                            icon = Icons.Default.QueuePlayNext,
                            onClickAction = { performAction(EpisodeViewModel.Action.PlayNextEpisode) },
                        ),
                        ContextMenuItem(
                            titleId = R.string.action_play_last,
                            icon = Icons.Default.AddToQueue,
                            onClickAction = { performAction(EpisodeViewModel.Action.PlayLastEpisode) },
                        ),
                        ContextMenuSeparator,
                        if (state.episode?.isSaved == false)
                            ContextMenuItem(
                                titleId = R.string.action_save_episode,
                                icon = Icons.Outlined.BookmarkAdd,
                                onClickAction = { performAction(EpisodeViewModel.Action.ToggleSaveEpisode) },
                            )
                        else
                            ContextMenuItem(
                                titleId = R.string.action_remove_saved_episode,
                                icon = Icons.Outlined.BookmarkRemove,
                                onClickAction = { performAction(EpisodeViewModel.Action.ToggleSaveEpisode) },
                            ),
                        ContextMenuSeparator,
                        ContextMenuItem(
                            titleId = R.string.action_share_episode,
                            icon = Icons.Default.Share,
                            onClickAction = { performAction(EpisodeViewModel.Action.ShareEpisode) },
                        )
                    )
                )
            }
        }

        /* description if present */
        item {
            EpisodeDescriptionContent(description = episode?.description)
        }

        // bottom app bar spacer
        item {
            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

@Composable
private fun EpisodeHeader(
    episode: Episode?,
    podcastData: Podcast?,
    listState: LazyListState,
    onPodcastClick: () -> Unit
) {
    val alphaLargeHeader = listState.expandedHeaderAlpha
    val bgDominantColor = podcastData?.artworkDominantColor
        ?.let { Color.getColor(it) }
        ?: MaterialTheme.colors.onSurface

    Column(
        modifier = Modifier
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
            .padding(horizontal = 16.dp)
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
                .align(Alignment.Start)
        ) {
            Image(
                painter = rememberCoilPainter(request = episode?.artworkUrl.orEmpty()),
                contentDescription = episode?.podcastName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
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
            } else {
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
            color = MaterialTheme.colors.primary
        )

        // date
        val context = LocalContext.current
        if (episode != null) {
            Text(
                text = episode.releaseDateTime.formatRelativeDateTime(context)
            )
        } else {
            Text(
                text = LoremIpsum(2).values.joinToString(" "),
                maxLines = 1,
                modifier = Modifier
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            )
        }
    }
}

@Composable
fun EpisodeTopAppBar(
    state: EpisodeViewModel.State,
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
                    Text(
                        text = state.episode?.name.orEmpty(),
                        maxLines = 2
                    )
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
