package com.caldeirasoft.outcast.ui.screen.episode

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.EpisodeWithPodcast
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.podcast.*
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsBottomSheet
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import com.caldeirasoft.outcast.ui.util.toDp
import com.caldeirasoft.outcast.ui.util.toPx
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.statusBarsPadding
import kotlinx.coroutines.launch

@Composable
fun EpisodeScreen(
    episode: Episode,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: EpisodeViewModel = mavericksViewModel(initialArgument = episode)
    EpisodeScreen(viewModel, navigateTo, navigateBack)
}

@Composable
fun EpisodeScreen(
    episode: EpisodeWithPodcast,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: EpisodeViewModel = mavericksViewModel(initialArgument = episode)
    EpisodeScreen(viewModel, navigateTo, navigateBack)
}

@Composable
fun EpisodeScreen(
    viewModel: EpisodeViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.collectAsState()
    val coroutineScope = rememberCoroutineScope()

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
                    navigateTo = navigateTo
                )
            }

            when {
                state.isLoading ->
                    item {
                        PodcastLoadingScreen()
                    }
                state.error != null ->
                    state.error?.let {
                        item {
                            ErrorScreen(t = it)
                        }
                    }
                else -> {
                    // buttons
                    item {
                        // buttons
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            PlayButton(episode = state.episode)
                            QueueButton(episode = state.episode)
                        }
                    }

                    /* description if present */
                    item {
                        state.episode.description?.let { description ->
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
            navigateUp = navigateBack
        )
    }
}

@Composable
private fun EpisodeExpandedHeader(
    state: EpisodeViewState,
    listState: LazyListState,
    navigateTo: (Screen) -> Unit,
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
                CoilImage(
                    data = state.episode.artworkUrl,
                    contentDescription = state.episode.podcastName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(72.dp)
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // episode name
            Box(modifier = Modifier.heightIn(min = 50.dp)) {
                Text(
                    text = state.episode.name,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // podcast name
            Text(
                text = with(AnnotatedString.Builder()) {
                    append(state.episode.podcastName)
                    append(" ›")
                    toAnnotatedString()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .clickable { state.podcast?.let { navigateTo(Screen.PodcastScreen(it)) } },
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
                    Text(text = state.episode.name)
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
