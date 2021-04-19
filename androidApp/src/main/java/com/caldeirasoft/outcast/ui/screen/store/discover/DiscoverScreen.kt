
package com.caldeirasoft.outcast.ui.screen.store.discover

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.interfaces.StoreItemArtwork
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.ifLoadingMore
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import com.caldeirasoft.outcast.ui.util.toDp
import com.caldeirasoft.outcast.ui.util.toPx
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.systemuicontroller.LocalSystemUiController
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter

@OptIn(ExperimentalPagerApi::class)
@ExperimentalAnimationApi
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun DiscoverScreen(
    storeDataArg: StoreDataArg?,
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: DiscoverViewModel = mavericksViewModel(initialArgument = storeDataArg)
    val state by viewModel.collectAsState()
    val lazyPagingItems = viewModel.discover.collectAsLazyPagingItems()
    val title = state.takeUnless { it.storeData == StoreData.Default }?.title
        ?: stringResource(id = R.string.store_tab_discover)

    RestoreStatusBarColorOnDispose()

    Scaffold {
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight
            val headerRatio: Float = 1 / 4f
            val headerHeight = remember { mutableStateOf((screenHeight * headerRatio).toInt()) }
            val spacerHeight = headerHeight.value

            LazyListLayout(lazyListItems = lazyPagingItems) {
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
                            .height(height = spacerHeight.toDp()))
                        {
                            DiscoverScreenHeader(
                                title = title,
                                state = state,
                                listState = listState
                            )
                        }
                    }

                    // label
                    item {

                    }

                    // description
                    state.storePage.description?.let { description ->
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

                    // content
                    when {
                        state.storePage.isMultiRoom -> {
                            items(lazyPagingItems = lazyPagingItems) { collection ->
                                when (collection) {
                                    is StoreCollectionFeatured ->
                                        StoreCollectionFeaturedContent(
                                            storeCollection = collection,
                                            navigateTo = navigateTo,
                                        )
                                    is StoreCollectionItems -> {
                                        // content
                                        StoreCollectionItemsContent(
                                            storeCollection = collection,
                                            navigateTo = navigateTo,
                                            followingStatus = state.followingStatus,
                                            onSubscribeClick = viewModel::subscribeToPodcast,
                                        )
                                    }
                                    is StoreCollectionData -> {
                                        // rooms
                                        StoreCollectionDataContent(
                                            storeCollection = collection,
                                            navigateTo = navigateTo
                                        )
                                    }
                                }
                            }
                        }
                        else -> {
                            items(lazyPagingItems = lazyPagingItems) { item ->
                                when (item) {
                                    is StorePodcast -> {
                                        PodcastListItem(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable(onClick = {
                                                    navigateTo(Screen.PodcastScreen(item))
                                                }),
                                            storePodcast = item,
                                            followingStatus = state.followingStatus[item.feedUrl],
                                            onSubscribeClick = viewModel::subscribeToPodcast
                                        )
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
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Divider()
                                    }
                                }
                            }
                        }
                    }

                    // loading more
                    lazyPagingItems.ifLoadingMore {
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
                DiscoverTopAppBar(
                    title = title,
                    state = state,
                    listState = listState)

                // refresh button
                RefreshButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 72.dp)
                        .navigationBarsPadding(),
                    discoverState = state,
                    listState = listState,
                    onClick = {
                        viewModel.clearNewVersionButton()
                        lazyPagingItems.refresh()
                    })
            }
        }
    }
}

@Composable
fun DiscoverScreenHeader(
    title: String,
    state: DiscoverState,
    listState: LazyListState,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val artwork = state.storePage.artwork
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
                contentDescription = state.storePage.label,
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
                    end = 16.dp)
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
fun DiscoverTopAppBar(
    title: String,
    state: DiscoverState,
    listState: LazyListState,
) {
    val appBarAlpha = listState.topAppBarAlpha
    val backgroundColor: Color = Color.blendARGB(
        MaterialTheme.colors.surface.copy(alpha = 0f),
        MaterialTheme.colors.surface,
        appBarAlpha)

    // top app bar
    val artwork = state.storePage.artwork
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


@OptIn(ExperimentalAnimationApi::class, InternalCoroutinesApi::class)
@Composable
fun RefreshButton(
    modifier: Modifier,
    discoverState: DiscoverState,
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
            .filter { discoverState.newVersionAvailable }
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
        visible = discoverState.newVersionAvailable)
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