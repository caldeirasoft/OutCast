package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun PodcastScreen(
    podcastArg: PodcastArg,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: PodcastViewModel = mavericksViewModel(initialArgument = podcastArg)
    val state by viewModel.collectAsState()

    PodcastScreen(
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PodcastScreen(
    state: PodcastViewState,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)


    ReachableScaffold(headerRatio = 1 / 3f) { headerHeight ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                PodcastExpandedHeader(
                    state = state,
                    listState = listState,
                    headerHeight = headerHeight
                )
            }

            item {
                // buttons
                Row(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    ActionChipButton(
                        selected = true,
                        onClick = { /*TODO*/ },
                        icon = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                            )
                        }
                    ) {
                        Text(text = stringResource(id = R.string.action_subscribe))
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                        )
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                        )
                    }
                }
            }

            // recent episodes
            item {
                StoreHeadingSectionWithLink(
                    title = stringResource(id = R.string.store_episodes),
                    onClick = { })
            }
            items(items = state.episodes) { episode ->
                EpisodeItem(
                    episode = episode,
                    onEpisodeClick = {
                        navigateTo(Screen.EpisodeScreen(episode.toEpisodeArg()))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                // bottom app bar spacer
                Spacer(modifier = Modifier.height(56.dp))
            }
        }

        ReachableAppBar(
            collapsedContent = {
                PodcastCollapsedHeader(
                    state = state,
                    listState = listState,
                    headerHeight = headerHeight,
                    navigateUp = navigateBack)
            },
            state = listState,
            headerHeight = headerHeight)
    }
}

@Composable
private fun PodcastExpandedHeader(
    state: PodcastViewState,
    listState: LazyListState,
    headerHeight: Int)
{
    val alphaLargeHeader = getExpandedHeaderAlpha(listState, headerHeight)
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight.toDp())
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopStart)
        ) {
            BoxWithConstraints {
                val bgDominantColor =
                    Color.getColor(state.podcast.artwork?.bgColor!!)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        //.background(Color.Magenta.copy(alpha = 0.9f))
                        .background(
                            brush = Brush.verticalGradient(
                                0.0f to bgDominantColor.copy(alpha = 0.5f),
                                0.2f to bgDominantColor.copy(alpha = 0.5f),
                                0.6f to Color.Transparent,
                                startY = 0.0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
                {
                    Log_D("HEIGHT",
                        this@BoxWithConstraints.constraints.maxHeight.toFloat().toString())
                }

            }
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 84.dp, bottom = 8.dp)
                .alpha(alphaLargeHeader)
        ) {
            Card(
                backgroundColor = Color.Transparent,
                shape = RoundedCornerShape(8.dp),
            ) {
                CoilImage(
                    data = state.podcast.artwork?.getArtworkPodcast().orEmpty(),
                    contentDescription = state.podcast.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                )
            }

            Column(modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
                .fillMaxHeight()) {
                Box(modifier = Modifier
                    .weight(1f)) {
                    AutoSizedText(
                        text = state.podcast.name,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxHeight(),
                        style = MaterialTheme.typography.h5,
                        maxFontSize = 35.sp,
                        minFontSize = 20.sp,
                        //color = Color.getColor(viewState.storePage.artwork?.textColor1!!)
                    )
                }
                Text(
                    state.podcast.artistName,
                    modifier = Modifier
                        .padding(bottom = 4.dp),
                    style = MaterialTheme.typography.body1,
                    maxLines = 2,
                    //color = Color.getColor(viewState.storePage.artwork?.textColor2!!)
                )
            }
        }
    }
}

@Composable
private fun PodcastCollapsedHeader(
    state: PodcastViewState,
    listState: LazyListState,
    headerHeight: Int,
    navigateUp: () -> Unit)
{
    val collapsedHeaderAlpha = getCollapsedHeaderAlpha(listState, headerHeight)
/*    // top app bar
    val artwork = viewState.storePage.artwork
    val contentEndColor = contentColorFor(MaterialTheme.colors.surface)
    val contentColor: Color =
        artwork?.textColor1
            ?.let {
                val contentStartColor = Color.getColor(it)
                Color.blendARGB(contentStartColor,
                    contentEndColor,
                    collapsedHeaderAlpha)
            }
            ?: contentEndColor*/

    val topAppBarBackgroudColor = Color.blendARGB(
        MaterialTheme.colors.background.copy(alpha = 0f),
        MaterialTheme.colors.background,
        collapsedHeaderAlpha)
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        title = {
            CompositionLocalProvider(LocalContentAlpha provides collapsedHeaderAlpha) {
                Text(text = state.podcast.name)
            }
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(Icons.Filled.ArrowBack,
                    contentDescription = null,)
            }
        },
        backgroundColor = topAppBarBackgroudColor,
        elevation = if (listState.firstVisibleItemIndex > 0) 1.dp else 0.dp
    )
}

