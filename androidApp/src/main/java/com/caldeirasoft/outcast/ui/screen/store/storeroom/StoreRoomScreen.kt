package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.annotation.FloatRange
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.request.ImageRequest
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@Composable
fun StoreRoomScreen(
    storeRoom: StoreRoom,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
)
{
    val viewModel: StoreRoomViewModel = viewModel(
        key = "store_room_${storeRoom.id}",
        factory = viewModelProviderFactoryOf { StoreRoomViewModel(storeRoom) }
    )
    val viewState by viewModel.state.collectAsState()
    StoreRoomScreen(
        title = storeRoom.label,
        viewState = viewState,
        discover = viewModel.discover,
        navigateToRoom = navigateToRoom,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreRoomScreen(
    title: String,
    viewState: StoreRoomViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    val listState = rememberLazyListState(0)
    val lazyPagingItems = discover.collectAsLazyPagingItems()

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)) {
            item {
                with(AmbientDensity.current) {
                    Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                }
            }

            DiscoverContents(
                lazyPagingItems = lazyPagingItems,
                loadingContent = { ShimmerStoreCollectionsList() },
            ) { lazyPagingItems ->
                when (viewState.storeData) {
                    is StoreMultiRoomPage ->
                        items(lazyPagingItems = lazyPagingItems) { item ->
                            when (item) {
                                is StoreCollectionPodcasts ->
                                    StoreCollectionPodcastsContent(
                                        storeCollection = item,
                                        navigateToRoom = navigateToRoom,
                                        navigateToPodcast = navigateToPodcast,
                                    )
                                is StoreCollectionEpisodes ->
                                    StoreCollectionEpisodesContent(
                                        storeCollection = item
                                    )
                                is StoreCollectionRooms ->
                                    StoreCollectionRoomsContent(
                                        storeCollection = item,
                                        navigateToRoom = navigateToRoom
                                    )
                            }
                        }
                    is StoreRoomPage ->
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
                                        .clickable(onClick = { navigateToPodcast(item.url) }),
                                    podcast = item
                                )
                            }
                        }
                }
            }
        }

        ReachableHeaderWithArtwork(
            title = { Text(text = title) },
            navigationIcon = {
                IconButton(onClick = navigateUp) {
                    Icon(Icons.Filled.ArrowBack)
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.Search)
                }
            },
            artwork = viewState.storeData.artwork,
            state = listState,
            headerHeight = headerHeight)
    }
}

@ExperimentalAnimationApi
@Composable
private fun ReachableHeaderWithArtwork(
    title: @Composable () -> Unit,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    artwork: Artwork?,
    state: LazyListState,
    headerHeight: Int,
) {
    with(AmbientDensity.current) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(headerHeight.toDp()))
        {
            val scrollAlpha =
                if (headerHeight != 0)
                    ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                        .coerceAtLeast(0f)
                else 1f
            val minimumHeight = 56.dp
            val computedHeight = (scrollAlpha * headerHeight).toDp().coerceAtLeast(minimumHeight)
            val backgroundColor: Color =
                artwork?.bgColor?.let { Color.getColor(it)}
                    ?: MaterialTheme.colors.background
            Box(modifier = Modifier
                .fillMaxWidth()
                .preferredHeightIn(max = computedHeight)
                .height(computedHeight)
                .background(MaterialTheme.colors.background)) {

                if (artwork != null) {
                    val artworkUrl = StoreItemWithArtwork.artworkUrl(artwork, 400, 196, crop = "fa")
                    CoilImage(
                        imageRequest = ImageRequest.Builder(AmbientContext.current)
                            .data(artworkUrl)
                            .crossfade(true)
                            .build(),
                        circularRevealedEnabled = true,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .preferredHeight(headerHeight.toDp())
                            .alpha(scrollAlpha),
                        loading = {
                            Box(modifier = Modifier.fillMaxSize()
                                .background(Color.DarkGray))
                        }
                    )
                } else {
                    // large title
                    Box(modifier = Modifier
                        .align(Alignment.Center)
                        .alpha(scrollAlpha)) {
                        ProvideTextStyle(typography.h4, title)
                    }
                }

                // top app bar
                val contentEndColor = contentColorFor(MaterialTheme.colors.surface)
                val contentColor: Color =
                    artwork?.textColor1
                        ?.let {
                            val contentStartColor = Color.getColor(it)
                            blendARGB(contentStartColor, contentEndColor, 1 - scrollAlpha)
                        }
                        ?: contentEndColor

                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    title = {
                        Providers(AmbientContentAlpha provides (1 - scrollAlpha)) {
                            title()
                        }
                    },
                    navigationIcon = navigationIcon,
                    actions = actions,
                    backgroundColor = Color.Transparent,
                    contentColor = contentColor,
                    elevation = if (state.firstVisibleItemIndex > 0) 1.dp else 0.dp
                )
            }
        }
    }
}

fun blendARGB(
    color1: Color, color2: Color,
    @FloatRange(from = 0.0, to = 1.0) ratio: Float,
): Color {
    val inverseRatio = 1 - ratio
    val a = color1.alpha * inverseRatio + color2.alpha * ratio
    val r =
        color1.red * inverseRatio + color2.red * ratio
    val g = color1.green * inverseRatio + color2.green * ratio
    val b = color1.blue * inverseRatio + color2.blue * ratio
    return Color(red = r, green = g, blue = b, alpha = a)
}


