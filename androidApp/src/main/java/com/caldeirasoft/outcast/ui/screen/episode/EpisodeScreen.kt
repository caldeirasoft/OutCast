package com.caldeirasoft.outcast.ui.screen.episode

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.coil.CoilImage

@Composable
fun EpisodeScreen(
    episodeArg: EpisodeArg,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: EpisodeViewModel = mavericksViewModel(initialArgument = episodeArg)
    val state by viewModel.collectAsState()

    EpisodeScreen(
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack)
}


@Composable
fun EpisodeScreen(
    state: EpisodeViewState,
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
                EpisodeExpandedHeader(
                    state = state,
                    listState = listState,
                    headerHeight = headerHeight
                )
            }

            item {
                // actions buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayButton(episode = state.episode)
                    QueueButton(episode = state.episode)
                }
            }

            // description if present
            item {
                state.episode.description?.let { description ->
                    Text(text = description,
                        textAlign = TextAlign.Justify)
                }
            }
        }

        ReachableAppBar(
            collapsedContent = {
                EpisodeCollapsedHeader(
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
private fun EpisodeExpandedHeader(
    state: EpisodeViewState,
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
                    Color.Red
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
                    data = state.episode.artworkUrl,
                    contentDescription = state.episode.podcastName,
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
                        text = state.episode.name,
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
                    state.episode.podcastName,
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
private fun EpisodeCollapsedHeader(
    state: EpisodeViewState,
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
                Text(text = state.episode.name)
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
