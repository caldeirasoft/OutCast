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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
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
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.podcast.GetPodcastVibrantColor
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.flow.collect

@Composable
fun EpisodeScreen(
    viewModel: EpisodeViewModel,
    storeEpisode: StoreEpisode? = null,
    fromSamePodcast: Boolean = false,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    EpisodeScreen(
        state = state,
        onPodcastClick = viewModel::openPodcastDetails,
        navigateBack = navigateBack
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
                    if (fromSamePodcast) navigateBack()
                    else navigateTo(Screen.PodcastScreen(event.podcast))
                }
            }
        }
    }
}

@Composable
fun EpisodeScreen(
    state: EpisodeViewState,
    navigateBack: () -> Unit,
    onPodcastClick: () -> Unit,
) {
    val dominantColor = remember(state.podcast) { GetPodcastVibrantColor(podcastData = state.podcast) }
    val dominantColorOrDefault = dominantColor ?: MaterialTheme.colors.primary

    Scaffold {
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
                            EpisodeExtendedActionButtons(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp),
                                episode = it,
                                onContextMenuClick = { }
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
            navigateBack = navigateBack,
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
    navigateBack: () -> Unit,
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
                IconButton(onClick = navigateBack) {
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
private fun EpisodeExtendedActionButtons(
    modifier: Modifier = Modifier,
    episode: Episode,
    onContextMenuClick: () -> Unit,
) {
    val tintColor = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.medium)
    Row(modifier = modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayButton(
            episode = episode)
        Spacer(modifier = Modifier.weight(1f))
        // queued button
        // favorite button
        // more button
        IconButton(onClick = onContextMenuClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = tintColor,
            )
        }
    }
}
