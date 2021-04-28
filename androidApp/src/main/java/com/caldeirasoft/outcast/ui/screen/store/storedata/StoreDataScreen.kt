package com.caldeirasoft.outcast.ui.screen.store.storedata

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.foundation.FilterChipGroup
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.podcast.PodcastActions
import com.caldeirasoft.outcast.ui.screen.podcastsettings.PodcastSettingsBottomSheet
import com.caldeirasoft.outcast.ui.screen.store.categories.CategoriesListBottomSheet
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.accompanist.systemuicontroller.LocalSystemUiController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

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
    StoreDataScreen(state = state, lazyPagingItems = lazyPagingItems) { action ->
        when(action) {
            is StoreDataActions.NavigateUp -> navigateBack()
            is StoreDataActions.OpenPodcastDetail -> navigateTo(Screen.PodcastScreen(action.storePodcast))
            is StoreDataActions.OpenEpisodeDetail -> navigateTo(Screen.EpisodeStoreScreen(action.storeEpisode))
            is StoreDataActions.OpenStoreData -> navigateTo(Screen.StoreDataScreen(action.storeData))
            else -> viewModel.submitAction(action)
        }
    }

    LaunchedEffect(state.categories) {
        if (state.categories.isNotEmpty()) {
            drawerContent.updateContent {
                CategoriesListBottomSheet(
                    categories = state.categories,
                    selectedCategory = state.currentCategory,
                ) { action ->
                    when (action) {
                        is StoreDataActions.NavigateUp -> coroutineScope.launch { drawerState.hide() }
                        else -> {
                            coroutineScope.launch { drawerState.hide() }
                            viewModel.submitAction(action)
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when(event) {
                is StoreDataEvent.OpenCategories -> this@LaunchedEffect.launch { drawerState.show() }
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
    actioner: (StoreDataActions) -> Unit,
) {
    val title = state.takeUnless { it.storeData == null }?.title
        ?: stringResource(id = R.string.store_tab_discover)

    RestoreStatusBarColorOnDispose()

    Scaffold {
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight
            val headerRatio: Float = 1 / 3f
            val headerHeight = remember { mutableStateOf((screenHeight * headerRatio).toInt()) }

            if (state.storeData?.containsFeatured == true)
                headerHeight.value = (screenHeight * 1 / 5f).toInt()

            val listState = rememberLazyListState()
            LazyColumn(
                state = listState,
                //verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .navigationBarsPadding()
                    .fillMaxSize())
            {
                // header
                item {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(height = headerHeight.value.toDp()))
                    {
                        StoreDataScreenHeader(
                            title = title,
                            state = state,
                            listState = listState
                        )
                    }
                }

                // label
                item {

                }

                // filter by category
                state.categories.takeIf { it.isNotEmpty()}
                    ?.let {
                        item {
                            LazyRow(contentPadding = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp)) {
                                item {
                                    ChipButton(
                                        selected = (state.currentCategoryId != DEFAULT_GENRE),
                                        onClick = { actioner(StoreDataActions.OpenCategories) })
                                    {
                                        Text(
                                            text = state.currentCategory.name
                                        )
                                    }
                                }
                            }
                        }
                    }

                // description
                state.storeData?.description?.let { description ->
                    item {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateContentSize()
                        ) {
                            OverflowText(text = description,
                                overflow = TextOverflow.Clip,
                                textAlign = TextAlign.Left,
                                maxLines = 3)
                        }
                    }
                }

                lazyPagingItems
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
                                                    actioner(
                                                        StoreDataActions.OpenStoreData(it)
                                                    )
                                                },
                                                openPodcastDetail = {
                                                    actioner(
                                                        StoreDataActions.OpenPodcastDetail(it)
                                                    )
                                                },
                                                openEpisodeDetail = {
                                                    actioner(
                                                        StoreDataActions.OpenEpisodeDetail(it)
                                                    )
                                                },
                                            )
                                        is StoreCollectionItems -> {
                                            // content
                                            StoreCollectionItemsContent(
                                                storeCollection = collection,
                                                openStoreDataDetail = {
                                                    actioner(
                                                        StoreDataActions.OpenStoreData(
                                                            it
                                                        )
                                                    )
                                                },
                                                openPodcastDetail = {
                                                    actioner(
                                                        StoreDataActions.OpenPodcastDetail(
                                                            it
                                                        )
                                                    )
                                                },
                                                openEpisodeDetail = {
                                                    actioner(
                                                        StoreDataActions.OpenEpisodeDetail(
                                                            it
                                                        )
                                                    )
                                                },
                                                followingStatus = state.followingStatus,
                                                followLoadingStatus = state.followLoadingStatus,
                                                onFollowPodcast = {
                                                    actioner(
                                                        StoreDataActions.FollowPodcast(
                                                            it
                                                        )
                                                    )
                                                },
                                            )
                                        }
                                        is StoreCollectionData -> {
                                            // rooms
                                            StoreCollectionDataContent(
                                                storeCollection = collection,
                                                openStoreDataDetail = {
                                                    actioner(
                                                        StoreDataActions.OpenStoreData(
                                                            it
                                                        )
                                                    )
                                                },
                                            )
                                        }
                                    }
                                }
                            }
                            else -> {
                                val sortByPopularity = (state.storeData?.sortByPopularity ?: false)
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
                                                            actioner(
                                                                StoreDataActions.OpenPodcastDetail(
                                                                    item
                                                                )
                                                            )
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
                                                        actioner(
                                                            StoreDataActions.FollowPodcast(
                                                                it
                                                            )
                                                        )
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
                                                        actioner(
                                                            StoreDataActions.OpenEpisodeDetail(
                                                                item
                                                            )
                                                        )
                                                    },
                                                    onThumbnailClick = {
                                                        actioner(
                                                            StoreDataActions.OpenPodcastDetail(
                                                                item.storePodcast
                                                            )
                                                        )
                                                    },
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

            // collapsing app bar
            StoreDataTopAppBar(
                title = title,
                state = state,
                listState = listState)

            // refresh button
            RefreshButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 72.dp)
                    .navigationBarsPadding(),
                storeDataState = state,
                listState = listState,
                onClick = {
                    actioner(StoreDataActions.ClearNotificationNewVersionAvailable)
                    lazyPagingItems.refresh()
                })
        }
    }
}

@Composable
fun StoreDataScreenHeader(
    title: String,
    state: StoreDataState,
    listState: LazyListState,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val artwork = state.storeData?.artwork
        if (artwork != null) {
            // Get the current SystemUiController
            val systemUiController = LocalSystemUiController.current
            val contentEndColor = contentColorFor(MaterialTheme.colors.surface)
            val contentColor =
                artwork.textColor1
                    ?.let {
                        val contentStartColor = Color.getColor(it)
                        Color.blendARGB(contentStartColor,
                            contentEndColor,
                            listState.topAppBarAlpha)
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
            CoilImage(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(listState.expandedHeaderAlpha),
                data = artworkUrl,
                contentDescription = state.storeData.label,
                contentScale = ContentScale.FillHeight,
                loading = {
                    Box(modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray))
                }
            )
        } else {
            // large title
            Box(modifier = Modifier
                .padding(
                    top = AppBarHeight,
                    start = 16.dp,
                    end = 16.dp
                )
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
                .align(Alignment.Center)
                .alpha(listState.expandedHeaderAlpha)) {
                ProvideTextStyle(typography.h4) {
                    Text(text = title, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun StoreDataTopAppBar(
    title: String,
    state: StoreDataState,
    listState: LazyListState,
) {
    val appBarAlpha = listState.topAppBarAlpha
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
                    Text(text = title)
                }
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
        Divider(modifier = Modifier.alpha(appBarAlpha))
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
    val searchOffsetY = remember { mutableStateOf(0) }
    val isButtonHidden = remember { mutableStateOf(false) }

    //listState.layoutInfo.visibleItemsInfo.first().size

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
    val systemUiController = LocalSystemUiController.current
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

    // Remember a PagerState with our tab count
    val pagerState = rememberPagerState(pageCount = storeCollection.items.size)

    Column {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalAlignment = Alignment.Start,
            verticalAlignment = Alignment.Top
        ) { page ->
            if (chunkedItems.indices.contains(page)) {
                val chartItems = chunkedItems[Math.floorMod(page, chunkedItems.size)]

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                    //.padding(horizontal = 4.dp)
                )
                {
                    chartItems.forEach { (index, storeItem) ->
                        StoreEpisodeItem(
                            episode = storeItem.episode,
                            modifier = Modifier.fillMaxWidth(),
                            onThumbnailClick = { openPodcastDetail(storeItem.storePodcast) },
                            onEpisodeClick = { openEpisodeDetail(storeItem) },
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
            val bgDominantColor = Color.getColor(item.artwork?.bgColor!!)
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
                CoilImage(
                    data = item.getArtworkFeaturedUrl(),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        }
    }
}
