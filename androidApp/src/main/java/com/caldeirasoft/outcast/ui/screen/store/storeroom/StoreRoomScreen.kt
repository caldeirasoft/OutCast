package com.caldeirasoft.outcast.ui.screen.store.storeroom

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
import androidx.compose.ui.gesture.scrollorientationlocking.Orientation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import coil.request.ImageRequest
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemWithArtwork
import com.caldeirasoft.outcast.domain.models.Artwork
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.AmbientBottomDrawerContent
import com.caldeirasoft.outcast.ui.navigation.AmbientBottomDrawerState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.openEpisodeDialog
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlin.math.log10

@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun StoreRoomScreen(
    storeRoom: StoreRoom,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
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
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreRoomScreen(
    title: String,
    viewState: StoreRoomViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)
    val lazyPagingItems = discover.collectAsLazyPagingItems()
    val drawerState = AmbientBottomDrawerState.current
    val drawerContent = AmbientBottomDrawerContent.current


    ReachableScaffold(
        headerRatioOrientation = Orientation.Vertical,
        headerRatio = 1/3f
    ) { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        // headerRatioOrientation = Orientation.Horizontal,
        // headerRatio = 13/32f

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
                    viewState.storePage.description?.let { description ->
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

                    when (val storePage = viewState.storePage) {
                        is StoreMultiRoomPage ->
                            items(lazyPagingItems = lazyPagingItems) { collection ->

                                when (collection) {
                                    is StoreCollectionItems -> {
                                        // header
                                        StoreHeadingSectionWithLink(
                                            title = collection.label,
                                            onClick = { navigateTo(Screen.Room(collection.room)) }
                                        )
                                        // content
                                        StoreCollectionItemsContent(
                                            storeCollection = collection,
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
                                                    .clickable(onClick = { navigateTo(Screen.StorePodcastScreen(item)) }),
                                                storePodcast = item,
                                                index = index + 1
                                            )
                                            Divider()
                                        }
                                        is StoreEpisode -> {
                                            EpisodeItemWithArtwork(
                                                onEpisodeClick = { openEpisodeDialog(drawerState, drawerContent, item) },
                                                onPodcastClick = { navigateTo(Screen.StorePodcastScreen(item.podcast)) },
                                                storeEpisode = item,
                                                index = index + 1
                                            )
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
                                                .clickable(onClick = { navigateTo(Screen.StorePodcastScreen(item)) }),
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
                with(AmbientDensity.current) {
                    val minimumHeight = 56.dp
                    val computedHeight = (scrollRatioHeaderHeight * headerHeight).toDp().coerceAtLeast(minimumHeight)
                    val artwork = viewState.storePage.artwork
                    if (artwork != null) {
                        val artworkUrl =
                            StoreItemWithArtwork.artworkUrl(artwork, 640, 260, crop = "fa")
                        Box(modifier = Modifier.fillMaxSize()) {
                            BoxWithConstraints {
                                val maxHeight = this.constraints.maxHeight
                                val maxWidth = this.constraints.maxWidth
                                val artworkHeight = maxWidth * 13f/32f
                                val bgColorHeight = (maxHeight - artworkHeight).coerceAtLeast(0f) + 20.dp.toIntPx()
                                val bgColorAspectRatio = maxWidth.toFloat() / bgColorHeight

                                Box(modifier = Modifier
                                    .fillMaxSize())
                                {
                                    CoilImage(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .preferredHeight(computedHeight)
                                            .alpha(alphaLargeHeader),
                                        imageRequest = ImageRequest.Builder(AmbientContext.current)
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


                            }
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
                            Text(text = title)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = navigateBack) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
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

@ExperimentalAnimationApi
@Composable
private fun ReachableAppBarWithBackground(
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
            val scrollRatioLargeHeader =
                if (headerHeight != 0)
                    ((headerHeight - (state.firstVisibleItemIndex * headerHeight.toFloat() + state.firstVisibleItemScrollOffset)) / headerHeight)
                        .coerceAtLeast(0f)
                else 1f
            val minimumHeight = 56.dp
            val computedHeight = (scrollRatioLargeHeader * headerHeight).toDp().coerceAtLeast(minimumHeight)
            val alphaLargeHeader = (3 * log10(scrollRatioLargeHeader.toDouble()) + 1).toFloat().coerceIn(0f, 1f)
            val alphaCollapsedHeader = (3 * log10((1-scrollRatioLargeHeader).toDouble()) + 1).toFloat().coerceIn(0f, 1f)
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
                            .alpha(alphaLargeHeader),
                        loading = {
                            Box(modifier = Modifier
                                .fillMaxSize()
                                .background(Color.DarkGray))
                        }
                    )
                } else {
                    // large title
                    Box(modifier = Modifier
                        .padding(bottom = (56.dp.toIntPx() * scrollRatioLargeHeader).toDp())
                        .align(Alignment.Center)
                        .alpha(alphaLargeHeader)) {
                        ProvideTextStyle(typography.h4, title)
                    }
                }

                // top app bar
                val contentEndColor = contentColorFor(MaterialTheme.colors.surface)
                val contentColor: Color =
                    artwork?.textColor1
                        ?.let {
                            val contentStartColor = Color.getColor(it)
                            Color.blendARGB(contentStartColor, contentEndColor, alphaCollapsedHeader)
                        }
                        ?: contentEndColor

                TopAppBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart),
                    title = {
                        Providers(LocalContentAlpha provides alphaCollapsedHeader) {
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


