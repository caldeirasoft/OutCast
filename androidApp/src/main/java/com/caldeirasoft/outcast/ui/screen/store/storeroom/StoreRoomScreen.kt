package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import coil.request.ImageRequest
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
@Composable
fun StoreRoomScreen(
    storeRoom: StoreRoom,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
)
{
    val viewModel: StoreRoomViewModel = mavericksViewModel(initialArgument = storeRoom)
    val state by viewModel.collectAsState()
    StoreRoomScreen(
        title = storeRoom.label,
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreRoomScreen(
    title: String,
    state: StoreRoomViewState,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)
    val lazyPagingItems = flowOf(state.discover).collectAsLazyPagingItems()

    ReachableScaffold(
        headerRatioOrientation = Orientation.Vertical,
        headerRatio = 1/3f
    ) { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)) {
            item {
                Spacer(modifier = Modifier.height(spacerHeight.toDp()))
            }

            lazyPagingItems
                .ifLoading {
                    item {
                        ShimmerStoreCollectionsList()
                    }
                }
                .ifError {
                    item {
                        ErrorScreen(t = it)
                    }
                }
                .ifNotLoading {
                    state.storePage.description?.let { description ->
                        item {
                            Text(text = description,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 8.dp,
                                    bottom = 8.dp))
                        }
                    }

                    when (val storePage = state.storePage) {
                        is StoreMultiRoomPage ->
                            items(lazyPagingItems = lazyPagingItems) { collection ->

                                when (collection) {
                                    is StoreCollectionPodcasts -> {
                                        // header
                                        StoreHeadingSectionWithLink(
                                            title = collection.label,
                                            onClick = { navigateTo(Screen.Room(collection.room)) }
                                        )

                                        // content
                                        StoreCollectionPodcastsContent(
                                            storeCollection = collection,
                                            navigateTo = navigateTo
                                        )
                                    }
                                    is StoreCollectionEpisodes -> {
                                        // header
                                        StoreHeadingSectionWithLink(
                                            title = collection.label,
                                            onClick = { navigateTo(Screen.Room(collection.room)) }
                                        )

                                        // content
                                        StoreCollectionEpisodesContent(
                                            storeCollection = collection,
                                            numRows = 3,
                                            navigateTo = navigateTo
                                        )
                                    }
                                    is StoreCollectionRooms -> {
                                        // header
                                        StoreHeadingSection(title = collection.label)
                                        // genres
                                        StoreCollectionRoomsContent(
                                            storeCollection = collection,
                                            navigateTo = navigateTo
                                        )
                                    }
                                    is StoreCollectionTopPodcasts -> {
                                        // header
                                        StoreHeadingSectionWithLink(
                                            title = collection.label,
                                            onClick = { navigateTo(Screen.Room(collection.room)) }
                                        )
                                        StoreCollectionTopPodcastsContent(
                                            storeCollection = collection,
                                            numRows = 4,
                                            navigateTo = navigateTo)
                                    }
                                    is StoreCollectionTopEpisodes -> {
                                        // header
                                        StoreHeadingSectionWithLink(
                                            title = collection.label,
                                            onClick = { navigateTo(Screen.Room(collection.room)) }
                                        )
                                        StoreCollectionTopEpisodesContent(
                                            storeCollection = collection,
                                            navigateTo = navigateTo)
                                    }
                                }
                            }
                        is StoreRoomPage ->
                            if (storePage.isIndexed) {
                                itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
                                    when (item) {
                                        is StorePodcast -> {
                                            PodcastListItemIndexed(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable(onClick = {
                                                        navigateTo(Screen.PodcastScreen(item))
                                                    }),
                                                storePodcast = item,
                                                index = index + 1
                                            )
                                            Divider()
                                        }
                                        is StoreEpisode -> {
                                            StoreEpisodeItem(
                                                modifier = Modifier,
                                                onEpisodeClick = {
                                                    navigateTo(Screen.EpisodeScreen(item.toEpisodeArg()))
                                                },
                                                onPodcastClick = {
                                                    navigateTo(Screen.PodcastScreen(item.podcast))
                                                },
                                                episode = item.episode,
                                                index = index + 1
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Divider()
                                        }
                                    }
                                }
                            }
                            else {
                                gridItems(
                                    lazyPagingItems = lazyPagingItems,
                                    contentPadding = PaddingValues(16.dp),
                                    horizontalInnerPadding = 8.dp,
                                    verticalInnerPadding = 8.dp,
                                    columns = 3
                                ) { item ->
                                    if (item is StorePodcast) {
                                        PodcastGridItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(onClick = {
                                                    navigateTo(Screen.PodcastScreen(item))
                                                }),
                                            podcast = item
                                        )
                                    }
                                }
                            }
                    }
                }
                .ifLoadingMore {
                    item {
                        Text(
                            modifier = Modifier.padding(
                                vertical = 16.dp,
                                horizontal = 4.dp
                            ),
                            text = "Loading next"
                        )
                    }
                }
        }

        ReachableAppBar(
            expandedContent = {
                val scrollRatioHeaderHeight = getScrollRatioHeaderHeight(listState, headerHeight)
                val alphaLargeHeader = getExpandedHeaderAlpha(listState, headerHeight)
                val minimumHeight = 56.dp
                val computedHeight = (scrollRatioHeaderHeight * headerHeight).toDp().coerceAtLeast(minimumHeight)
                val artwork = state.storePage.artwork
                if (artwork != null) {
                    val artworkUrl =
                        StoreItemWithArtwork.artworkUrl(artwork, 640, 260, crop = "fa")
                    Box(modifier = Modifier.fillMaxSize()) {
                        CoilImage(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(computedHeight)
                                .alpha(alphaLargeHeader),
                            imageRequest = ImageRequest.Builder(LocalContext.current)
                                .data(artworkUrl)
                                .crossfade(true)
                                .build(),
                            circularRevealedEnabled = true,
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.DarkGray))
                            }
                        )
                    }
                } else {
                    // large title
                    Box(modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp)
                        .align(Alignment.Center)
                        .alpha(alphaLargeHeader)) {
                        ProvideTextStyle(typography.h4) {
                            Text(text = title, textAlign = TextAlign.Center)
                        }
                    }
                }
            },
            collapsedContent = {
                val collapsedHeaderAlpha = getCollapsedHeaderAlpha(listState, headerHeight)
                // top app bar
                val artwork = state.storePage.artwork
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
                        CompositionLocalProvider(LocalContentAlpha provides collapsedHeaderAlpha) {
                            Text(text = title)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.Filled.ArrowBack,
                                contentDescription = null,)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Filled.Search,
                                contentDescription = null,)
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
