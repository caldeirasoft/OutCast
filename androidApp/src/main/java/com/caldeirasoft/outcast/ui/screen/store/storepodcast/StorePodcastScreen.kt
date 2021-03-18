package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.mvrx.Fail
import com.airbnb.mvrx.Loading
import com.airbnb.mvrx.Success
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionPodcasts
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.util.Log_D
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastViewState
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf

@ExperimentalCoroutinesApi
@Composable
fun StorePodcastScreen(
    storePodcast: StorePodcast,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: StorePodcastViewModel = mavericksViewModel(initialArgument = storePodcast)
    val state by viewModel.collectAsState()

    StorePodcastScreen(
        state = state,
        navigateTo = navigateTo,
        navigateBack = navigateBack,
        showAllEpisodes = viewModel::showAllEpisodes
    )
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StorePodcastScreen(
    state: StorePodcastViewState,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
    showAllEpisodes: () -> Unit,
) {
    val listState = rememberLazyListState(0)
    val otherPodcastsLazyPagingItems = flowOf(state.otherPodcasts).collectAsLazyPagingItems()

    ReachableScaffold(headerRatio = 1 / 3f) { headerHeight ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                StorePodcastExpandedHeader(
                    state = state,
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
                        icon = {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                            )
                        }
                    ) {
                        Text(text = stringResource(id = R.string.action_subscribe))
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                        )
                    }

                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                        )
                    }
                }
            }

            when (val storePageAsync = state.podcastPageAsync) {
                is Loading ->
                    item {
                        ShimmerStoreCollectionsList()
                    }
                is Fail ->
                    item {
                        ErrorScreen(t = storePageAsync.error)
                    }
                is Success -> {

                    val storePodcastPage = storePageAsync.invoke()
                    val podcastData = storePodcastPage.podcast

                    /* description if present */
                    item {
                        podcastData.description?.let { description ->
                            PodcastDescriptionContent(description = description)

                            podcastData.genre?.let { genre ->
                                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                    ChipButton(selected = false,
                                        onClick = { navigateTo(Screen.GenreScreen(genre)) }) {
                                        Text(text = genre.name)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    // episodes
                    item {
                        StoreHeadingSectionWithLink(
                            title = stringResource(id = R.string.store_episodes),
                            onClick = { })
                    }
                    if (state.showAllEpisodes || state.episodes.size < 5) {
                        items(items = state.episodes) { episode ->
                            EpisodeItem(
                                episode = episode,
                                onEpisodeClick = {
                                    navigateTo(Screen.EpisodeScreen(episode.toEpisodeArg()))
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                        }
                    } else {
                        // show first, last and "show more" button
                        item {
                            // most recent episode
                            val firstEpisode = state.episodes.first()
                            EpisodeItem(
                                episode = firstEpisode,
                                onEpisodeClick = {
                                    navigateTo(Screen.EpisodeScreen(firstEpisode.toEpisodeArg()))
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                        }

                        item {
                            // text button "more..."
                            TextButton(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .align(Alignment.Center),
                                onClick = showAllEpisodes)
                            {
                                Text(text = stringResource(id = R.string.action_show_all_episodes),
                                    style = typography.button.copy(letterSpacing = 0.25.sp))
                            }
                            Divider()
                        }

                        item {
                            // oldest episode
                            val lastEpisode = state.episodes.last()
                            EpisodeItem(
                                episode = lastEpisode,
                                onEpisodeClick = {
                                    navigateTo(Screen.EpisodeScreen(lastEpisode.toEpisodeArg()))
                                }
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Divider()
                        }
                    }

                    // related podcasts
                    items(lazyPagingItems = otherPodcastsLazyPagingItems) { collection ->
                        when (collection) {
                            is StoreCollectionPodcasts -> {
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
                                // content
                                StoreCollectionPodcastsContent(
                                    storeCollection = collectionWithTitle,
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
        }

        ReachableAppBar(
            collapsedContent = {
                StorePodcastCollapsedHeader(
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
private fun StorePodcastExpandedHeader(
    state: StorePodcastViewState,
    listState: LazyListState,
    headerHeight: Int,
) {
    val podcastData = state.podcastPageAsync.invoke()?.podcast
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
                    Color.getColor(podcastData?.artwork?.bgColor!!)
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
                    imageModel = podcastData?.artwork?.getArtworkPodcast().orEmpty(),
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
                        text = podcastData?.name.orEmpty(),
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
                    podcastData?.artistName.orEmpty(),
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
private fun StorePodcastCollapsedHeader(
    state: StorePodcastViewState,
    listState: LazyListState,
    headerHeight: Int,
    navigateUp: () -> Unit,
) {
    val podcastData = state.podcastPageAsync.invoke()?.podcast
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
                Text(text = podcastData?.name.orEmpty())
            }
        },
        navigationIcon = {
            IconButton(onClick = navigateUp) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        backgroundColor = topAppBarBackgroudColor,
        elevation = if (listState.firstVisibleItemIndex > 0) 1.dp else 0.dp
    )
}

@Composable
private fun PodcastDescriptionContent(description: String) {
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
