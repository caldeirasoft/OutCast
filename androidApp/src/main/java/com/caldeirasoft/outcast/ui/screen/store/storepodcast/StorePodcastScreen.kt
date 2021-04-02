package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.annotation.StringRes
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
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
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.models.PodcastPage
import com.caldeirasoft.outcast.domain.models.store.StoreCollectionPodcasts
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastArg
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import com.caldeirasoft.outcast.ui.util.toDp
import com.caldeirasoft.outcast.ui.util.toPx
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import timber.log.Timber

enum class StorePodcastTabs(@StringRes val titleId: Int) {
    Episodes(R.string.podcast_episodes),
    RelatedPodcasts(R.string.podcast_youMayAlsoLike)
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@ExperimentalCoroutinesApi
@Composable
fun StorePodcastScreen(
    storePodcastArg: StorePodcastArg,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    var tabIndex by remember { mutableStateOf(0) }
    val viewModel: StorePodcastViewModel = mavericksViewModel(initialArgument = storePodcastArg)
    val state by viewModel.collectAsState()

    val otherPodcastsLazyPagingItems = flowOf(state.otherPodcasts).collectAsLazyPagingItems()

    ReachableScaffold(headerRatio = 2 / 5f) { headerHeight ->
        val storePageAsync = state.podcastPageAsync
        val storePodcastPage = storePageAsync.invoke() ?: storePodcastArg.toStorePodcast().page
        val podcastData = storePodcastPage.podcast

        //
        val listState = rememberLazyListState(0)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                StorePodcastExpandedHeader(
                    podcastPage = storePodcastPage,
                    listState = listState,
                    headerHeight = headerHeight,
                    navigateTo = navigateTo
                )
            }

            when (val status = state.podcastPageAsync) {
                is Loading -> {
                    item {
                        StorePodcastLoadingScreen()
                    }
                }
                is Fail -> {
                    item {
                        ErrorScreen(t = status.error)
                    }
                }
                is Success -> {
                    // buttons
                    item {
                        // buttons
                        BoxWithConstraints(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            contentAlignment = Alignment.Center) {
                            val fullWidth = constraints.maxWidth
                            val buttonWidth = 150.dp.toPx()
                            val twoButtonsWidth = 2 * buttonWidth + 20.dp.toPx()
                            val edgePadding = (fullWidth - buttonWidth) / 2
                            val edgePadding2Btns = (fullWidth - twoButtonsWidth) / 2
                            // settings
                            OutlinedButton(
                                modifier = Modifier
                                    .width(150.dp)
                                    .graphicsLayer(
                                        translationX = animateFloatAsState(
                                            targetValue = if (state.followingStatus == FollowStatus.FOLLOWED) edgePadding - edgePadding2Btns else 0f,
                                            animationSpec = tween(durationMillis = 750)
                                        ).value,
                                        alpha = animateFloatAsState(
                                            targetValue = if (state.followingStatus == FollowStatus.FOLLOWED) 1f else 0f,
                                            animationSpec = tween(durationMillis = 750)
                                        ).value
                                    ),
                                onClick = { },
                                contentPadding = PaddingValues(start = 24.dp,
                                    end = 24.dp,
                                    top = 8.dp,
                                    bottom = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = stringResource(id = R.string.action_settings),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(text = stringResource(id = R.string.action_settings),
                                        style = typography.button.copy(letterSpacing = 0.5.sp))
                                }
                            }

                            // following buttons
                            OutlinedButton(
                                modifier = Modifier
                                    .width(150.dp)
                                    .graphicsLayer(
                                        translationX = animateFloatAsState(
                                            targetValue = if (state.followingStatus == FollowStatus.FOLLOWED) edgePadding2Btns - edgePadding else 0f,
                                            animationSpec = tween(durationMillis = 750)
                                        ).value,
                                        alpha = animateFloatAsState(
                                            targetValue = if (state.followingStatus == FollowStatus.FOLLOWED) 1f else 0f,
                                            animationSpec = tween(durationMillis = 750)
                                        ).value
                                    ),
                                onClick = { viewModel.unfollow() },
                                contentPadding = PaddingValues(start = 24.dp,
                                    end = 24.dp,
                                    top = 8.dp,
                                    bottom = 8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = stringResource(id = R.string.action_following),
                                        modifier = Modifier.padding(end = 4.dp)
                                    )
                                    Text(text = stringResource(id = R.string.action_following),
                                        style = typography.button.copy(letterSpacing = 0.5.sp))
                                }
                            }

                            // follow button
                            AnimatedVisibility(
                                visible = (state.followingStatus != FollowStatus.FOLLOWED),
                                enter = fadeIn(animationSpec = tween(durationMillis = 250)),
                                exit = fadeOut(animationSpec = tween(durationMillis = 250)))
                            {
                                Button(
                                    modifier = Modifier
                                        .width(150.dp),
                                    onClick = {
                                        if (state.followingStatus == FollowStatus.UNFOLLOWED)
                                            viewModel.subscribe()
                                    },
                                    contentPadding = PaddingValues(start = 24.dp,
                                        end = 24.dp,
                                        top = 8.dp,
                                        bottom = 8.dp),
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Crossfade(targetState = state.followingStatus) { followStatus ->
                                            when (followStatus) {
                                                FollowStatus.FOLLOWING ->
                                                    LinearProgressIndicator(
                                                        color = contentColorFor(MaterialTheme.colors.primary),
                                                        modifier = Modifier
                                                            .size(24.dp)
                                                            .padding(end = 4.dp))
                                                FollowStatus.UNFOLLOWED ->
                                                    Icon(
                                                        imageVector = Icons.Default.Add,
                                                        contentDescription = stringResource(id = R.string.action_follow),
                                                        modifier = Modifier.padding(end = 4.dp)
                                                    )
                                            }
                                        }
                                        Text(text = stringResource(id = R.string.action_follow),
                                            style = typography.button.copy(letterSpacing = 0.5.sp))
                                    }
                                }
                            }
                        }
                    }

                    /* description if present */
                    item {
                        podcastData.description?.let { description ->
                            PodcastDescriptionContent(description = description)
                        }
                    }

                    // tabs
                    stickyHeader {
                        podcastData.genre?.let { genre ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ChipButton(selected = false,
                                    onClick = { navigateTo(Screen.GenreScreen(genre)) }) {
                                    Text(text = genre.name)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        ScrollableTabRow(
                            modifier = Modifier.fillMaxWidth(),
                            selectedTabIndex = tabIndex,
                            backgroundColor = MaterialTheme.colors.surface,
                            edgePadding = 0.dp,
                            divider = { }
                        )
                        {
                            StorePodcastTabs.values().forEachIndexed { index, tab ->
                                Tab(selected = (index == tab.ordinal),
                                    onClick = { tabIndex = tab.ordinal },
                                    text = {
                                        Text(
                                            text = stringResource(id = tab.titleId),
                                            style = MaterialTheme.typography.body2)
                                    }
                                )
                            }
                        }
                        Divider(modifier = Modifier.fillMaxWidth())
                    }

                    when (tabIndex) {
                        StorePodcastTabs.Episodes.ordinal -> {
                            // episodes
                            item {
                                StoreHeadingSection(
                                    title = stringResource(id = R.string.podcast_x_episodes,
                                        state.episodes.size))
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
                                    // text button "show more episodes"
                                    TextButton(
                                        modifier = Modifier
                                            .padding(horizontal = 16.dp)
                                            .align(Alignment.Center),
                                        onClick = viewModel::showAllEpisodes)
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
                        }

                        StorePodcastTabs.RelatedPodcasts.ordinal -> {
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
                    podcastData = podcastData,
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
    podcastPage: PodcastPage,
    navigateTo: (Screen) -> Unit,
    listState: LazyListState,
    headerHeight: Int,
) {
    val podcastData = podcastPage.podcast
    val alphaLargeHeader = getExpandedHeaderAlpha(listState, headerHeight)
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(headerHeight.toDp())
    )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val bgDominantColor =
                Color.getColor(podcastData.artwork?.bgColor!!)
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
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(top = 56.dp, bottom = 8.dp)
                .alpha(alphaLargeHeader),
        ) {
            // thumbnail
            Card(
                backgroundColor = Color.Transparent,
                shape = RoundedCornerShape(8.dp),
                elevation = 2.dp,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterHorizontally)
            ) {
                CoilImage(
                    imageModel = podcastData.artwork?.getArtworkPodcast().orEmpty(),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // podcast
            Text(
                text = podcastData.name,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
            )

            // artist name + link
            val clickableArtistMod = podcastPage.artist?.let {
                Modifier.clickable { navigateTo(Screen.Room(it)) }
            } ?: Modifier

            Text(
                text = with(AnnotatedString.Builder()) {
                    append(podcastData.artistName)
                    podcastPage.artist?.let {
                        append(" ›")
                    }
                    toAnnotatedString()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
                    .then(clickableArtistMod),
                style = MaterialTheme.typography.body1,
                maxLines = 2,
                color = podcastPage.artist?.let { MaterialTheme.colors.primary }
                    ?: Color.Unspecified,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StorePodcastCollapsedHeader(
    podcastData: Podcast,
    listState: LazyListState,
    headerHeight: Int,
    navigateUp: () -> Unit,
) {
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
                Text(text = podcastData.name)
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
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            .animateContentSize()
    ) {
        OverflowText(text = description,
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Justify,
            maxLines = 3)
    }
}

@Composable
private fun PodcastDescriptionRelatedShowsContent(
    podcastData: Podcast,
    followedShows: StoreCollectionPodcasts?,
    navigateTo: (Screen) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflow by remember { mutableStateOf(false) }
    val maxHeight = 60.dp
    val maxHeightInPx = maxHeight.toPx()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (!isExpanded) Modifier.heightIn(max = maxHeight) else Modifier)
            .animateContentSize()
    ) {
        BoxWithConstraints() {
            val screenHeight = constraints.maxHeight
            Timber.d("screenHeight: ${screenHeight}")
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // description
                Text(text = podcastData.description.orEmpty(),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clickable(onClick = { isExpanded = isExpanded.not() }),
                    overflow = TextOverflow.Clip,
                    textAlign = TextAlign.Justify)

                // genre
                podcastData.genre?.let { genre ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                        ChipButton(selected = false,
                            onClick = { navigateTo(Screen.GenreScreen(genre)) }) {
                            Text(text = genre.name)
                        }
                    }
                }

                /*
                followedShows?.let {
                    val bgDominantColor =
                        podcastData.artwork?.bgColor?.let { Color.getColor(it).copy(alpha = 0.2f) } ?: Color.Unspecified
                    // podcasts also followed
                    Column(modifier = Modifier.background(bgDominantColor)) {
                        StoreCollectionPodcastsContent(
                            storeCollection = followedShows.copy(
                                label = stringResource(id = R.string.podcast_podcastsListenersAlsoFollow)
                            ),
                            navigateTo = navigateTo
                        )
                    }
                }
                 */
            }

            Timber.d("boxHeight2: ${constraints.maxHeight}")
            Timber.d("boxHeight3: ${maxHeightInPx}")
            if ((constraints.maxHeight <= maxHeightInPx) && isExpanded.not()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            brush = Brush.verticalGradient(
                                0.0f to Color.Transparent,
                                0.4f to MaterialTheme.colors.background,
                                startY = 0.0f,
                                endY = Float.POSITIVE_INFINITY
                            )
                        )
                )
                {
                    // text button "more..."
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        //contentPadding = ButtonDefaults.TextButtonContentPadding.copy(top = 0.dp, bottom = 0.dp),
                        contentPadding = PaddingValues(0.dp),
                        onClick = { isExpanded = isExpanded.not() })
                    {
                        Text(text = stringResource(id = R.string.action_show_more),
                            style = typography.button.copy(letterSpacing = 0.25.sp))
                    }
                }
            }
        }
    }
}


@Composable
fun StorePodcastLoadingScreen() =
    LoadingListShimmer { list, floatAnim ->
        val brush = Brush.verticalGradient(list, 0f, floatAnim)
        Column(modifier = Modifier
            .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            // description
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(3) {
                    Spacer(modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .background(brush = brush))
                }
            }

            // episodes
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // header
                Spacer(modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(fraction = 0.6f)
                    .height(25.dp)
                    .background(brush = brush))

                repeat(6) {
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth(),
                        text = {
                            Spacer(modifier = Modifier
                                .fillMaxWidth()
                                .height(14.dp)
                                .background(brush = brush))
                        },
                        secondaryText = {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                repeat(3) {
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .background(brush = brush))
                                }
                            }
                        },
                        icon = {
                            Spacer(modifier = Modifier
                                .size(40.dp)
                                .background(brush = brush))
                        }
                    )
                }
            }
        }
    }
