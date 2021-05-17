package com.caldeirasoft.outcast.ui.screen.store.storedata

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import androidx.paging.compose.itemsIndexed
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.common.Constants.Companion.DEFAULT_GENRE
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.*
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.store.categories.CategoriesListBottomSheet
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.*
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalAnimationApi::class, FlowPreview::class)
@Composable
fun StoreDataScreen(
    viewModel: StoreDataViewModel,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val lazyPagingItems = viewModel.discover.collectAsLazyPagingItems()

    StoreDataScreen(
        state = state,
        lazyPagingItems = lazyPagingItems,
        navigateBack = navigateBack,
        navigateTo = navigateTo,
        onFollowPodcast = viewModel::followPodcast,
        onCategoryClick = {
            coroutineScope.OpenBottomSheetCategoriesFilter(
                state = state,
                drawerState = drawerState,
                drawerContent = drawerContent,
                onCategorySelected = viewModel::selectCategoryFilter
            )
        },
    )

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when(event) {
                is StoreDataEvent.OpenCategories ->
                    this@LaunchedEffect.launch { drawerState.show() }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@ExperimentalAnimationApi
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreDataScreen(
    state: StoreDataState,
    lazyPagingItems: LazyPagingItems<StoreItem>,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
    onFollowPodcast: (StorePodcast) -> Unit,
    onCategoryClick: () -> Unit
) {
    val title = when (state.storeData) {
        StoreData.Default, null -> stringResource(id = R.string.store_tab_discover)
        StoreData.TopCharts -> stringResource(id = R.string.store_tab_charts)
        else -> state.title
    }
    RestoreStatusBarColorOnDispose()

    Scaffold {
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight
            val headerRatio: Float = 1 / 3f
            val headerHeight = remember { mutableStateOf((screenHeight * headerRatio).toInt()) }

            val collapsingToolbarState = rememberCollapsingToolbarState()

            AppbarContainer(
                modifier = Modifier.fillMaxWidth(),
                scrollStrategy = ScrollStrategy.ExitUntilCollapsed,
                collapsingToolbarState = collapsingToolbarState
            ) {
                CollapsingToolbar(collapsingToolbarState = collapsingToolbarState) {
                    var textSize by remember { mutableStateOf(25.sp) }
                    var paddingStart by remember { mutableStateOf(16.dp) }
                    val artwork = state.storeData?.artwork

                    Text(
                        text = title,
                        modifier = Modifier
                            .heightIn(min = AppBarHeight)
                            .road(Alignment.CenterStart, Alignment.BottomStart)
                            .progress { value ->
                                textSize = (18 + (36 - 18) * value).sp
                                paddingStart = (16.dp + ((1 - value) * 44).dp)
                            }
                            .padding(top = 16.dp, bottom = 16.dp)
                            .padding(start = paddingStart, end = 16.dp)
                            .statusBarsPadding()
                            .then(artwork
                                ?.let { Modifier.alpha(collapsingToolbarState.collapsedAlpha)}
                                ?: Modifier),
                        fontSize = textSize
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = headerHeight.value.toDp())
                            .pin()
                    ) {
                    }

                    if (artwork != null) {
                        HeaderEditorialArtwork(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(height = headerHeight.value.toDp())
                                .parallax()
                                .alpha(collapsingToolbarState.expandedAlpha),
                            artwork = artwork
                        )
                    }

                    StoreDataScreenTopAppBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .navigationBarsPadding(bottom = false)
                            .pin(),
                        state = state,
                        collapsingToolbarState = collapsingToolbarState
                    )
                }

                val listState = lazyPagingItems.rememberLazyListStateWithPagingItems()
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .appBarBody()
                )
                {
                    // filter
                    item {
                        // filter by category
                        if (state.categories.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                            ) {
                                ChipButton(
                                    selected = (state.currentCategoryId != DEFAULT_GENRE),
                                    onClick = onCategoryClick)
                                {
                                    Text(
                                        text = state.currentCategory.name
                                    )
                                }
                            }
                        }
                    }

                    // label
                    item {

                    }

                    // description
                    item {
                        state.storeData?.description?.let { description ->
                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .animateContentSize()
                            ) {
                                OverflowText(
                                    text = description,
                                    overflow = TextOverflow.Clip,
                                    textAlign = TextAlign.Left,
                                    maxLines = 3
                                )
                            }
                        }
                    }

                    lazyPagingItems
                        .ifLoading {
                            item {
                                LoadingScreen()
                            }
                        }
                        .ifError {
                            item {
                                ErrorScreen(t = it)
                            }
                        }
                        .ifNotLoading {
                            // content
                            when {
                                state.storeData != null && state.storeData.isMultiRoom -> {
                                    items(lazyPagingItems = lazyPagingItems) { collection ->
                                        when (collection) {
                                            is StoreCollectionFeatured ->
                                                StoreCollectionFeaturedContent(
                                                    storeCollection = collection,
                                                    openStoreDataDetail = {
                                                        navigateTo(Screen.StoreDataScreen(it))
                                                    },
                                                    openPodcastDetail = {
                                                        navigateTo(Screen.PodcastScreen(it))
                                                    },
                                                    openEpisodeDetail = {
                                                        navigateTo(Screen.EpisodeScreen(it))
                                                    },
                                                )
                                            is StoreCollectionItems -> {
                                                // content
                                                StoreCollectionItemsContent(
                                                    storeCollection = collection,
                                                    openStoreDataDetail = {
                                                        navigateTo(Screen.StoreDataScreen(it))
                                                    },
                                                    openPodcastDetail = {
                                                        navigateTo(Screen.PodcastScreen(it))
                                                    },
                                                    openEpisodeDetail = {
                                                        navigateTo(Screen.EpisodeScreen(it))
                                                    },
                                                    followingStatus = state.followingStatus,
                                                    followLoadingStatus = state.followLoadingStatus,
                                                    onFollowPodcast = {
                                                        onFollowPodcast(it)
                                                    },
                                                )
                                            }
                                            is StoreCollectionData -> {
                                                // rooms
                                                StoreCollectionDataContent(
                                                    storeCollection = collection,
                                                    openStoreDataDetail = {
                                                        navigateTo(Screen.StoreDataScreen(it))
                                                    },
                                                    openPodcastDetail = {
                                                        navigateTo(Screen.PodcastScreen(it))
                                                    },
                                                    openEpisodeDetail = {
                                                        navigateTo(Screen.EpisodeScreen(it))
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                                else -> {
                                    val sortByPopularity =
                                        (state.storeData?.sortByPopularity ?: false)
                                    when (lazyPagingItems.peek(0)) {
                                        is StorePodcast -> gridItemsIndexed(
                                            lazyPagingItems = lazyPagingItems,
                                            contentPadding = PaddingValues(16.dp),
                                            horizontalInnerPadding = 16.dp,
                                            verticalInnerPadding = 16.dp,
                                            columns = 2
                                        ) { index, item ->
                                            when (item) {
                                                is StorePodcast -> {
                                                    PodcastGridItem(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clickable(onClick = {
                                                                navigateTo(Screen.PodcastScreen(item))
                                                            }),
                                                        podcast = item,
                                                        index = (index + 1).takeIf { sortByPopularity },
                                                        isFollowing = state.followingStatus.contains(
                                                            item.id
                                                        ),
                                                        isFollowingLoading = state.followLoadingStatus.contains(
                                                            item.id
                                                        ),
                                                        onFollowPodcast = {
                                                            onFollowPodcast(it)
                                                        },
                                                    )
                                                }
                                            }
                                        }
                                        is StoreEpisode -> itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
                                            when (item) {
                                                is StoreEpisode -> {
                                                    StoreEpisodeItem(
                                                        modifier = Modifier,
                                                        onEpisodeClick = {
                                                            navigateTo(Screen.EpisodeScreen(item))
                                                        },
                                                        onThumbnailClick = {
                                                            navigateTo(Screen.PodcastScreen(item.storePodcast))
                                                        },
                                                        onContextMenuClick = { },
                                                        episode = item.episode,
                                                        index = (index + 1).takeIf { sortByPopularity },
                                                    )
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    Divider()
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        .ifLoadingMore {
                            // loading more
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
            }


            // refresh button
            RefreshButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
                    .navigationBarsPadding(),
                storeDataState = state,
                listState = rememberLazyListState(),
                onClick = {
                    //actioner(StoreDataActions.ClearNotificationNewVersionAvailable)
                    lazyPagingItems.refresh()
                })
        }
    }
}

@Composable
fun HeaderEditorialArtwork(
    modifier: Modifier,
    artwork: Artwork,
) {
    // Get the current SystemUiController
    val systemUiController = rememberSystemUiController()
    val contentEndColor = contentColorFor(MaterialTheme.colors.surface)
    val contentColor =
        artwork.textColor1
            ?.let {
                val contentStartColor = Color.getColor(it)
                Color.blendARGB(
                    contentStartColor,
                    contentEndColor,
                    1f
                )
            } ?: contentEndColor
    val useDarkIcons = contentColor.luminance() < 0.5f
    SideEffect {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light artwork
        systemUiController.setStatusBarColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons
        )
    }

    val artworkUrl =
        StoreItemArtwork.artworkUrl(artwork, 640, 260, crop = "fa")
    Image(
        painter = rememberCoilPainter(request = artworkUrl),
        modifier = modifier,
        contentDescription = null,
        contentScale = ContentScale.FillHeight,
    )
}

@Composable
private fun StoreDataScreenTopAppBar(
    modifier: Modifier,
    state: StoreDataState,
    collapsingToolbarState: CollapsingToolbarState
) {
    val appBarAlpha = collapsingToolbarState.collapsedAlpha
    val backgroundColor: Color = Color.blendARGB(
        MaterialTheme.colors.surface.copy(alpha = 0f),
        MaterialTheme.colors.surface,
        appBarAlpha)

    // top app bar
    val artwork = state.storeData?.artwork
    val contentEndColor = contentColorFor(MaterialTheme.colors.surface)
    val contentColor: Color =
        artwork?.textColor1
            ?.let {
                val contentStartColor = Color.getColor(it)
                Color.blendARGB(contentStartColor,
                    contentEndColor,
                    appBarAlpha)
            }
            ?: contentEndColor

    Column(
        modifier = modifier
    ) {
        TopAppBar(
            modifier = Modifier,
            title = {
            },
            navigationIcon = {
                IconButton(onClick = { }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null)
                }
            },
            actions = { },
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp
        )

        if (collapsingToolbarState.progress == 0f)
            Divider()
    }
}

@OptIn(ExperimentalAnimationApi::class, InternalCoroutinesApi::class, FlowPreview::class)
@Composable
fun RefreshButton(
    modifier: Modifier,
    storeDataState: StoreDataState,
    listState: LazyListState,
    onClick: () -> Unit,
) {
    val offsetY = remember { mutableStateOf(0) }
    val oldIndex = remember { mutableStateOf(0) }
    val isButtonHidden = remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .filter { storeDataState.newVersionAvailable }
            .filter { listState.isScrollInProgress }
            .debounce(100)
            .collect {
                val indexOffset = oldIndex.value - listState.firstVisibleItemIndex
                val scrollOffset = offsetY.value - listState.firstVisibleItemScrollOffset
                if ((indexOffset > 0) || (indexOffset == 0 && scrollOffset > 0)) {
                    isButtonHidden.value = false
                } else if ((indexOffset < 0) || (indexOffset == 0 && scrollOffset < 0)) {
                    isButtonHidden.value = true
                }
                offsetY.value = listState.firstVisibleItemScrollOffset
                oldIndex.value = listState.firstVisibleItemIndex
            }
    }

    val translationValue = if (isButtonHidden.value) 72.dp.toPx() else 0f
    // refresh button
    AnimatedVisibility(
        modifier = modifier
            .graphicsLayer(
                translationY = animateFloatAsState(
                    targetValue = translationValue,
                    animationSpec = tween(durationMillis = 750)
                ).value,
            ),
        visible = storeDataState.newVersionAvailable)
    {
        Button(
            onClick = onClick,
        ) {
            Text(text = stringResource(id = R.string.action_tap_to_refresh),
                style = typography.button.copy(letterSpacing = 0.5.sp))
        }
    }
}

@Composable
fun RestoreStatusBarColorOnDispose()
{
    // Get the current SystemUiController
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = MaterialTheme.colors.isLight
    DisposableEffect(Unit) {
        // Update all of the system bar colors to be transparent, and use
        // dark icons if we're in light theme
        onDispose {
            systemUiController.setStatusBarColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }
    }
}

@Composable
fun StoreCollectionItemsContent(
    storeCollection: StoreCollectionItems,
    followingStatus: List<Long> = emptyList(),
    followLoadingStatus: List<Long> = emptyList(),
    openStoreDataDetail: (StoreData) -> Unit,
    openPodcastDetail: (StorePodcast) -> Unit,
    openEpisodeDetail: (StoreEpisode) -> Unit,
    onFollowPodcast: (StorePodcast) -> Unit = { },
) {
    StoreHeadingSectionWithLink(
        title = storeCollection.label,
        onClick = {
            openStoreDataDetail(storeCollection.room)
        })

    if (storeCollection.items.filterIsInstance<StorePodcast>().isNotEmpty()) {
        StoreCollectionPodcastContent(
            storeCollection = storeCollection,
            followingStatus = followingStatus,
            followLoadingStatus = followLoadingStatus,
            openPodcastDetail = openPodcastDetail,
            onFollowPodcast = onFollowPodcast
        )
    }
    else if (storeCollection.items.filterIsInstance<StoreEpisode>().isNotEmpty()) {
        StoreCollectionEpisodeContent(
            storeCollection = storeCollection,
            openPodcastDetail = openPodcastDetail,
            openEpisodeDetail = openEpisodeDetail,
        )
    }
}

@Composable
fun StoreCollectionPodcastContent(
    storeCollection: StoreCollectionItems,
    openPodcastDetail: (StorePodcast) -> Unit,
    followingStatus: List<Long> = emptyList(),
    followLoadingStatus: List<Long> = emptyList(),
    onFollowPodcast: (StorePodcast) -> Unit = { },
) {
    val listState = rememberLazyListState()
    // content
    LazyRow(
        state = listState,
        modifier = Modifier.nestedScroll(listState.nestedScrollConnection),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(items = storeCollection.items) { index, item ->
            when (item) {
                is StorePodcast ->
                    PodcastGridItem(
                        modifier = Modifier
                            .width(150.dp)
                            .clickable(onClick = { openPodcastDetail(item) }),
                        podcast = item,
                        index = if (storeCollection.sortByPopularity) index + 1 else null,
                        isFollowing = followingStatus.contains(item.id),
                        isFollowingLoading = followLoadingStatus.contains(item.id),
                        onFollowPodcast = onFollowPodcast
                    )
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StoreCollectionEpisodeContent(
    storeCollection: StoreCollectionItems,
    openPodcastDetail: (StorePodcast) -> Unit,
    openEpisodeDetail: (StoreEpisode) -> Unit,
) {
    val numRows = 3
    val indexedItems = storeCollection.items
            .filterIsInstance<StoreEpisode>()
            .mapIndexed { index, storeItem -> Pair(index, storeItem) }
    val chunkedItems = indexedItems.chunked(numRows)

    val listState = rememberLazyListState()
    // content
    BoxWithConstraints {
        val contentWidth = constraints.maxWidth
        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(listState.nestedScrollConnection),
            contentPadding = PaddingValues(end = (0.1f * contentWidth).dp, bottom = 16.dp),
        ) {
            items(items = chunkedItems) { chartItems ->
                Column(
                    modifier = Modifier
                        .width((0.9f * contentWidth).toDp())
                )
                {
                    chartItems.forEach { (index, storeItem) ->
                        StoreEpisodeItem(
                            episode = storeItem.episode,
                            modifier = Modifier.fillMaxWidth(),
                            onThumbnailClick = { openPodcastDetail(storeItem.storePodcast) },
                            onEpisodeClick = { openEpisodeDetail(storeItem) },
                            onContextMenuClick = { },
                            index = if (storeCollection.sortByPopularity) (index + 1) else null
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun StoreCollectionFeaturedContent(
    storeCollection: StoreCollectionFeatured,
    openStoreDataDetail: (StoreData) -> Unit,
    openPodcastDetail: (StorePodcast) -> Unit,
    openEpisodeDetail: (StoreEpisode) -> Unit,
) {
    // Remember a PagerState with our tab count
    val pagerState = rememberPagerState(pageCount = storeCollection.items.size)

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2.03f)
        ) { page ->
            val item = storeCollection.items[Math.floorMod(page, storeCollection.items.size)]
            val bgDominantColor = Color.getColor(item.artwork?.bgColor) ?: Color.Unspecified
            Card(
                backgroundColor = bgDominantColor,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxSize(0.95f)
                    .padding(horizontal = 4.dp)
                    .clickable {
                        when (item) {
                            is StoreData -> openStoreDataDetail(item)
                            is StorePodcast -> openPodcastDetail(item)
                            is StoreEpisode -> openEpisodeDetail(item)
                        }
                    }
            )
            {
                Image(
                    painter = rememberCoilPainter(request = item.getArtworkFeaturedUrl()),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun StoreCollectionDataContent(
    storeCollection: StoreCollectionData,
    openStoreDataDetail: (StoreData) -> Unit,
    openPodcastDetail: (StorePodcast) -> Unit,
    openEpisodeDetail: (StoreEpisode) -> Unit,
) {
    // header
    StoreHeadingSection(title = storeCollection.label)

    // room content
    LazyRow(
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items = storeCollection.items) { item ->
            Card(
                backgroundColor = colors[0],
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(200.dp)
                    .clickable(onClick = {
                        when (item) {
                            is StoreData -> openStoreDataDetail(item)
                            is StorePodcast -> openPodcastDetail(item)
                            is StoreEpisode -> openEpisodeDetail(item)
                        }
                    })
            )
            {
                Image(
                    painter = rememberCoilPainter(request = item.getArtworkFeaturedUrl()),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(18 / 9f)
                )
            }
        }
    }
}

fun CoroutineScope.OpenBottomSheetCategoriesFilter(
    state: StoreDataState,
    drawerState: ModalBottomSheetState,
    drawerContent: ModalBottomSheetContent,
    onCategorySelected: (StoreCategory) -> Unit
)
{
    drawerContent.updateContent {
        CategoriesListBottomSheet(
            categories = state.categories,
            selectedCategory = state.currentCategory,
            navigateUp = { launch { drawerState.hide() } },
            onCategorySelected = onCategorySelected
        )
    }
    this.launch {
        delay(500)
        drawerState.show()
    }
}
