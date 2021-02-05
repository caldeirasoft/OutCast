package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StorePodcastPage
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastEpisodesScreen(
    storePodcast: StorePodcast,
    navigateToPodcast: (StorePodcast) -> Unit,
    navigateToEpisode: (StoreEpisode) -> Unit,
    navigateUp: () -> Unit
) {
    val viewModel: StorePodcastViewModel = viewModel(
        key = storePodcast.url,
        factory = viewModelProviderFactoryOf { StorePodcastViewModel(storePodcast) }
    )
    val viewState by viewModel.state.collectAsState()

    StorePodcastEpisodesScreen(
        viewState = viewState,
        navigateToPodcast = navigateToPodcast,
        navigateToEpisode = navigateToEpisode,
        navigateUp = navigateUp)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StorePodcastEpisodesScreen(
    viewState: StorePodcastViewModel.State,
    navigateToPodcast: (StorePodcast) -> Unit,
    navigateToEpisode: (StoreEpisode) -> Unit,
    navigateUp: () -> Unit
) {
    val listState = rememberLazyListState(1)

    ReachableScaffold() { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, bottom = 56.dp)) {

            item {
                with(AmbientDensity.current) {
                    Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                }
            }

            viewState.storeResourceData
                .onLoading {
                    item {
                        ShimmerStoreCollectionsList()
                    }
                }
                .onSuccess<StorePodcastPage> { it ->
                    // recent episodes
                    item {
                        StoreHeadingSection(title = "All episodes")
                    }
                    items(it.episodes) {
                        EpisodeItem(
                            storeEpisode = it,
                            onEpisodeClick = { /*TODO*/ })

                        Divider()
                    }
                }
        }

        ReachableAppBar(
            expandedContent = {
                val scrollRatioHeaderHeight = getScrollRatioHeaderHeight(listState, headerHeight)
                val alphaLargeHeader = getExpandedHeaderAlpha(listState, headerHeight)
                with(AmbientDensity.current) {
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
                            WithConstraints {
                                val bgDominantColor = Color.getColor(viewState.storePage.artwork?.bgColor!!)
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
                                        constraints.maxHeight.toFloat().toString())
                                }

                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 56.dp)
                                .alpha(alphaLargeHeader)
                        ) {
                            Card(
                                backgroundColor = Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                CoilImage(
                                    imageModel = viewState.storePage.getArtworkUrl(),
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
                                    .weight(1f)
                                    .background(Color.Yellow)) {
                                    Text(
                                        viewState.storePage.name,
                                        modifier = Modifier
                                            .align(Alignment.CenterStart),
                                        style = MaterialTheme.typography.h5,
                                        maxLines = 2,
                                        color = Color.getColor(viewState.storePage.artwork?.textColor1!!)
                                    )
                                }
                                Text(
                                    viewState.storePage.artistName,
                                    modifier = Modifier
                                        .padding(bottom = 4.dp),
                                    style = MaterialTheme.typography.body1,
                                    maxLines = 2,
                                    color = Color.getColor(viewState.storePage.artwork?.textColor2!!)
                                )
                            }
                        }
                    }
                }
            },
            collapsedContent = {
                val collapsedHeaderAlpha = getCollapsedHeaderAlpha(listState, headerHeight)
                // top app bar
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
                        ?: contentEndColor

                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    title = {
                        Providers(AmbientContentAlpha provides collapsedHeaderAlpha) {
                            Text(text = viewState.storePage.name)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateUp) {
                            Icon(Icons.Filled.ArrowBack)
                        }
                    },
                    backgroundColor = Color.Transparent,
                    contentColor = contentColor,
                    elevation = if (listState.firstVisibleItemIndex > 0) 1.dp else 0.dp
                )
            },
            state = listState,
            headerHeight = headerHeight)
    }
}
