package com.caldeirasoft.outcast.ui.screen.store.storepodcast

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import androidx.compose.ui.layout.WithConstraints
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastScreen(
    storePodcast: StorePodcast,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: StorePodcastViewModel = viewModel(
        key = storePodcast.url,
        factory = viewModelProviderFactoryOf { StorePodcastViewModel(storePodcast) }
    )
    val viewState by viewModel.state.collectAsState()

    StorePodcastScreen(
        viewState = viewState,
        otherPodcasts = viewModel.otherPodcasts,
        navigateTo = navigateTo,
        navigateBack = navigateBack)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StorePodcastScreen(
    viewState: StorePodcastViewModel.State,
    otherPodcasts: Flow<PagingData<StoreItem>>,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val listState = rememberLazyListState(0)
    val podcastData = viewState.storePage.storeData
    val otherPodcastsLazyPagingItems = otherPodcasts.collectAsLazyPagingItems()

    ReachableScaffold(headerRatio = 1/3f) { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                StorePodcastExpandedHeader(
                    viewState = viewState,
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
                        icon = { Icon(Icons.Default.CheckCircle)}
                    ) {
                        Text(text = stringResource(id = R.string.action_subscribe))
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Public)
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(imageVector = Icons.Default.Share)
                    }
                }
            }

            // description if present
            podcastData.description?.let { description ->
                item {
                    StorePodcastDescriptionContent(description = description)

                    podcastData.genre?.let { genre ->
                        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                            ChipButton(selected = false,
                                onClick = { navigateTo(Screen.Genre(genre.id, genre.name)) }) {
                                Text(text = genre.name)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            viewState.storeResourceData
                .onLoading {
                    item {
                        ShimmerStoreCollectionsList()
                    }
                }
                .onSuccess<StorePodcastPage> {
                    // trailer
                    it.storeEpisodeTrailer?.let {
                        item {
                            // trailer header
                            StoreHeadingSection(
                                title = stringResource(id = R.string.store_trailer))
                            // trailer
                            TrailerItem(
                                storeEpisode = it,
                                onEpisodeClick = { /*TODO*/ })
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // recent episodes
                    item {
                        StoreHeadingSectionWithLink(
                            title = stringResource(id = R.string.store_episodes),
                            onClick = { navigateTo(Screen.StoreEpisodesScreen(podcastData)) })
                    }
                    items(it.recentEpisodes) {
                        EpisodeDetailItem(
                            storeEpisode = it,
                            onEpisodeClick = { /*TODO*/ })

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // other podcasts
                    items(lazyPagingItems = otherPodcastsLazyPagingItems) { collection ->
                        when (collection) {
                            is StoreCollectionItems -> {
                                val collectionWithTitle = collection.copy(
                                    label = when (collection.label) {
                                        "podcastsByArtist" -> stringResource(id = R.string.podcast_podcastsByArtist,
                                            podcastData.artistName)
                                        "podcastsListenersAlsoFollow" -> stringResource(id = R.string.podcast_podcastsListenersAlsoFollow)
                                        "topPodcastsInGenre" -> stringResource(id = R.string.podcast_topPodcastsInGenre,
                                            podcastData.genre?.name.orEmpty())
                                        else -> "-"
                                    },
                                )
                                StoreHeadingSectionWithLink(
                                    title = collectionWithTitle.label,
                                    onClick = { navigateTo(Screen.Room(collectionWithTitle.room)) }
                                )
                                // content
                                StoreCollectionItemsContent(
                                    storeCollection = collection,
                                    navigateTo = navigateTo
                                )
                            }
                        }
                    }

                    item {
                        // bottom app bar spacer
                        Spacer(modifier = Modifier.height(56.dp))
                    }
                }
        }

        ReachableAppBar(
            collapsedContent = {
                StorePodcastCollapsedHeader(
                    viewState = viewState,
                    listState = listState,
                    headerHeight = headerHeight,
                    navigateUp = navigateBack)
            },
            state = listState,
            headerHeight = headerHeight)
    }
}

@Composable
fun StorePodcastExpandedHeader(
    viewState: StorePodcastViewModel.State,
    listState: LazyListState,
    headerHeight: Int)
{
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
                    val bgDominantColor =
                        Color.getColor(viewState.storePage.artwork?.bgColor!!)
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
                    .padding(horizontal = 16.dp)
                    .padding(top = 84.dp, bottom = 8.dp)
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
                        .weight(1f)) {
                        AutoSizedText(
                            text = viewState.storePage.name,
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
                        viewState.storePage.artistName,
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
}

@Composable
fun StorePodcastCollapsedHeader(
    viewState: StorePodcastViewModel.State,
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
            Providers(AmbientContentAlpha provides collapsedHeaderAlpha) {
                Text(text = viewState.storePage.name)
            }
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(Icons.Filled.ArrowBack)
            }
        },
        backgroundColor = topAppBarBackgroudColor,
        elevation = if (listState.firstVisibleItemIndex > 0) 1.dp else 0.dp
    )
}

@Composable
private fun StorePodcastDescriptionContent(description: String) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp)
            .animateContentSize()
    ) {
        OverflowText(text = description,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Justify,
            maxLines = 3)
    }
}
