@file:OptIn(KoinApiExtension::class)

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
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastArg.Companion.toStorePodcastArg
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.CoilImage
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import org.koin.core.component.KoinApiExtension
import timber.log.Timber

@OptIn(ExperimentalPagerApi::class)
@ExperimentalAnimationApi
@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun DiscoverScreen(
    storeDataArg: StoreDataArg?,
    navigateTo: (Screen) -> Unit,
) {
    Timber.d("DBG - StoreDirectoryScreen recompose")
    val viewModel: DiscoverViewModel = mavericksViewModel(initialArgument = storeDataArg)
    val state by viewModel.collectAsState()
    val lazyPagingItems = viewModel.discover.collectAsLazyPagingItems()
    val coroutineScope = rememberCoroutineScope()
    val title = state.takeUnless { it.storeData == StoreData.Default }?.title
        ?: stringResource(id = R.string.store_tab_discover)

    Scaffold {
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight
            val screenWidth = constraints.maxWidth
            val headerRatio: Float = 1 / 5f
            val headerHeight = remember { mutableStateOf((screenHeight * headerRatio).toInt()) }
            val spacerHeight = headerHeight.value - 56.px

            LazyListLayout(lazyListItems = lazyPagingItems) {
                val listState = rememberLazyListState()
                LazyColumn(
                    state = listState,
                    //verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(top = 56.dp)
                        .fillMaxSize())
                {
                    // header
                    item {
                        Spacer(modifier = Modifier
                            .height(spacerHeight.toDp())
                            .animateContentSize())
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
                                                    navigateTo(Screen.StorePodcastScreen(item.toStorePodcastArg()))
                                                }),
                                            storePodcast = item,
                                            followingStatus = state.followingStatus[item.id],
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
                                                navigateTo(Screen.StorePodcastScreen(
                                                    item.podcast.toStorePodcastArg()))
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
                ReachableAppBar(
                    expandedContent = {
                        val scrollRatioHeaderHeight =
                            getScrollRatioHeaderHeight(listState, headerHeight.value)
                        val alphaLargeHeader = getExpandedHeaderAlpha(listState, headerHeight.value)
                        val minimumHeight = 56.dp
                        val computedHeight =
                            (scrollRatioHeaderHeight * headerHeight.value).toDp()
                                .coerceAtLeast(minimumHeight)
                        val artwork = state.storePage.artwork
                        if (artwork != null) {
                            val artworkUrl =
                                StoreItemArtwork.artworkUrl(artwork, 640, 260, crop = "fa")
                            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                                val boxWidth = constraints.maxWidth
                                val artworkRatio: Float = 13 / 32f
                                headerHeight.value = (boxWidth * artworkRatio).toInt()
                                CoilImage(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(computedHeight)
                                        .alpha(alphaLargeHeader),
                                    data = artworkUrl,
                                    contentDescription = state.storePage.label,
                                    contentScale = ContentScale.FillWidth,
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
                        val collapsedHeaderAlpha =
                            getCollapsedHeaderAlpha(listState, headerHeight.value)
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
                                IconButton(onClick = { }) {
                                    Icon(
                                        Icons.Filled.ArrowBack,
                                        contentDescription = null,
                                    )
                                }
                            },
                            actions = { },
                            backgroundColor = Color.Transparent,
                            contentColor = contentColor,
                            elevation = if (listState.firstVisibleItemIndex > 0) 1.dp else 0.dp
                        )
                    },
                    state = listState,
                    headerHeight = headerHeight.value)

                // refresh button
                RefreshButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 72.dp),
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
