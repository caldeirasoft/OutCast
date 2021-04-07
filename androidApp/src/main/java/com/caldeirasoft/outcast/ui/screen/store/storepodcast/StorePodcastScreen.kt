package com.caldeirasoft.outcast.ui.screen.podcast

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.db.Podcast
import com.caldeirasoft.outcast.domain.model.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResource
import com.caldeirasoft.outcast.ui.components.foundation.quantityStringResourceZero
import com.caldeirasoft.outcast.ui.components.preferences.PreferenceScreen
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.base.FollowStatus
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastArg
import com.caldeirasoft.outcast.ui.screen.store.storepodcast.StorePodcastViewState
import com.caldeirasoft.outcast.ui.theme.blendARGB
import com.caldeirasoft.outcast.ui.theme.getColor
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import com.caldeirasoft.outcast.ui.util.toDp
import com.caldeirasoft.outcast.ui.util.toPx
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch


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
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current

    val episodesLazyPagingItems = viewModel.episodes.collectAsLazyPagingItems()
    val otherPodcastsLazyPagingItems = flowOf(state.otherPodcasts).collectAsLazyPagingItems()
    val podcastData = state.podcast

    LaunchedEffect(key1 = drawerContent) {
        drawerContent.updateContent {
            PodcastSettingsBottomSheet(
                podcastId = podcastData.podcastId,
                viewModel = viewModel)
        }
    }

    ReachableScaffold(headerRatio = 2 / 5f) { headerHeight ->
        //
        val listState = rememberLazyListState(0)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                StorePodcastExpandedHeader(
                    state = state,
                    listState = listState,
                    headerHeight = headerHeight,
                    navigateTo = navigateTo
                )
            }

            when {
                state.isLoading ->
                    item {
                        StorePodcastLoadingScreen()
                    }
                state.error != null ->
                    state.error?.let {
                        item {
                            ErrorScreen(t = it)
                        }
                    }
                else -> {
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
                            FollowingButton(
                                textContent = stringResource(id = R.string.action_settings),
                                imageVector = Icons.Default.Settings,
                                translationValue = if (state.followingStatus == FollowStatus.FOLLOWED) edgePadding - edgePadding2Btns else 0f,
                                alphaValue = if (state.followingStatus == FollowStatus.FOLLOWED) 1f else 0f,
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.show()
                                    }
                                }
                            )

                            // following buttons
                            FollowingButton(
                                textContent = stringResource(id = R.string.action_following),
                                imageVector = Icons.Default.Check,
                                translationValue = if (state.followingStatus == FollowStatus.FOLLOWED) edgePadding2Btns - edgePadding else 0f,
                                alphaValue = if (state.followingStatus == FollowStatus.FOLLOWED) 1f else 0f,
                                onClick = { viewModel.unfollow() }
                            )

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

                    // genre
                    item {
                        podcastData.genre?.let { genre ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                ChipButton(selected = false,
                                    onClick = { navigateTo(Screen.GenreScreen(genre)) }) {
                                    Text(text = genre.name)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // episodes
                    item {
                        StoreHeadingSection(
                            title = stringResource(id = R.string.podcast_x_episodes,
                                episodesLazyPagingItems.itemCount))
                    }
                    if (state.showAllEpisodes || episodesLazyPagingItems.itemCount < 5) {
                        items(lazyPagingItems = episodesLazyPagingItems) { episode ->
                            episode?.let {
                                EpisodeItem(
                                    episode = episode,
                                    onEpisodeClick = {
                                        navigateTo(Screen.EpisodeScreen(episode.toEpisodeArg()))
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider()
                            }
                        }
                    } else {
                        // show first, last and "show more" button
                        item {
                            // most recent episode
                            episodesLazyPagingItems[0]?.let { firstEpisode ->
                                EpisodeItem(
                                    episode = firstEpisode,
                                    onEpisodeClick = {
                                        navigateTo(Screen.EpisodeScreen(firstEpisode.toEpisodeArg()))
                                    }
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider()
                            }
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
                            val size = episodesLazyPagingItems.itemCount - 1
                            episodesLazyPagingItems.peek(size)?.let { lastEpisode ->
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
    state: StorePodcastViewState,
    navigateTo: (Screen) -> Unit,
    listState: LazyListState,
    headerHeight: Int,
) {
    val podcastData = state.podcast
    val artistRoom = state.artistRoom
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
            val clickableArtistMod = artistRoom?.let {
                Modifier.clickable { navigateTo(Screen.Room(it)) }
            } ?: Modifier

            Text(
                text = with(AnnotatedString.Builder()) {
                    append(podcastData.artistName)
                    artistRoom?.let {
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
                color = artistRoom?.let { MaterialTheme.colors.primary } ?: Color.Unspecified,
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
fun StorePodcastLoadingScreen() {
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
}


@ExperimentalCoroutinesApi
@Composable
fun PodcastSettingsBottomSheet(
    podcastId: Long,
    viewModel: StorePodcastViewModel,
) {
    val listState = rememberLazyListState(0)
    val coroutineScope = rememberCoroutineScope()
    val drawerState = LocalBottomSheetState.current
    Column()
    {
        TopAppBar(
            title = {
                Text(text = stringResource(id = R.string.podcast_settings))
            },
            navigationIcon = {
                IconButton(onClick = {
                    coroutineScope.launch {
                        drawerState.hide()
                    }
                }) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            },
            backgroundColor = Color.Transparent,
            elevation = 0.dp
            //elevation = if (scrollState.value > 0) 1.dp else 0.dp
        )

        val prefs by viewModel.dataStore.data.collectAsState(initial = null)
        val customEffectsEnabled =
            (prefs?.get(booleanPreferencesKey("$podcastId:pref_custom_playback_effects")) == true)
        PreferenceScreen(
            dataStore = viewModel.dataStore,
            items = listOf(
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_new_episodes),
                    summary = stringResource(R.string.settings_new_episodes_desc),
                    key = "$podcastId:pref_new_episodes",
                    singleLineTitle = true,
                    icon = Icons.Default.Inbox,
                    entries = mapOf(
                        "INBOX" to stringResource(R.string.settings_new_episodes_inbox),
                        "QUEUE_NEXT" to stringResource(R.string.settings_new_episodes_queue_next),
                        "QUEUE_LAST" to stringResource(R.string.settings_new_episodes_queue_last),
                        "ARCHIVE" to stringResource(R.string.settings_new_episodes_archive)
                    )
                ),
                SwitchPreferenceItem(
                    title = stringResource(R.string.settings_notifications),
                    summary = stringResource(R.string.settings_notifications_desc),
                    key = "$podcastId:pref_notify",
                    singleLineTitle = true,
                    icon = Icons.Default.Notifications,
                ),
                // episode limit : no limit/1/2/5/10 most recents
                SingleListPreferenceItem(
                    title = stringResource(R.string.settings_episode_limit),
                    summary = stringResource(R.string.settings_episode_limit_desc),
                    key = "$podcastId:pref_episode_limit",
                    singleLineTitle = true,
                    icon = Icons.Default.Inbox,
                    defaultValue = "0",
                    entries =
                    listOf(0, 1, 2, 3, 5, 10)
                        .map {
                            it.toString() to quantityStringResourceZero(
                                R.plurals.settings_episode_limit_x_episodes,
                                R.string.settings_episode_no_limit,
                                it, it)
                        }
                        .toMap()
                ),
                // playback effects (custom)
                SwitchPreferenceItem(
                    title = stringResource(R.string.settings_playback_effects),
                    summary = stringResource(R.string.settings_playback_effects_desc),
                    key = "$podcastId:pref_custom_playback_effects",
                    singleLineTitle = true,
                    icon = Icons.Default.Notifications,
                    defaultValue = false,
                ),
                //  -> playback speed
                NumberRangePreferenceItem(
                    title = stringResource(R.string.settings_playback_speed),
                    summary = "",
                    key = "$podcastId:pref_playback_speed",
                    singleLineTitle = true,
                    icon = Icons.Default.Speed,
                    visible = customEffectsEnabled,
                    defaultValue = 1.0f,
                    steps = 0.1f,
                    valueRange = 0.5f..3.0f,
                    valueRepresentation = { value -> "%.1f x".format(value) }
                ),
                //  -> trim silence
                SwitchPreferenceItem(
                    title = stringResource(R.string.settings_trim_silence),
                    summary = "",
                    key = "$podcastId:pref_trim_silence",
                    singleLineTitle = true,
                    icon = Icons.Default.ContentCut,
                    defaultValue = false,
                    visible = customEffectsEnabled
                ),
                // skip intro
                NumberPreferenceItem(
                    title = stringResource(R.string.settings_skip_intro),
                    summary = "",
                    key = "$podcastId:pref_skip_intros",
                    singleLineTitle = true,
                    icon = Icons.Default.SkipNext,
                    valueRepresentation = { value ->
                        quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
                    }
                ),
                // skip end
                NumberPreferenceItem(
                    title = stringResource(R.string.settings_skip_ending),
                    summary = "",
                    key = "$podcastId:pref_skip_ending",
                    singleLineTitle = true,
                    icon = Icons.Default.SkipNext,
                    valueRepresentation = { value ->
                        quantityStringResource(R.plurals.settings_skip_x_seconds, value, value)
                    }
                ),
                // unfollow
                ActionPreferenceItem(
                    title = stringResource(id = R.string.settings_unfollow),
                    key = "unfollow",
                    singleLineTitle = true,
                    icon = Icons.Default.Unsubscribe,
                    action = {
                        viewModel.unfollow()
                        coroutineScope.launch {
                            drawerState.hide()
                        }
                    }
                )
            ))
    }
}

@Composable
fun FollowingButton(
    textContent: String,
    imageVector: ImageVector,
    translationValue: Float,
    alphaValue: Float,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = Modifier
            .width(150.dp)
            .graphicsLayer(
                translationX = animateFloatAsState(
                    targetValue = translationValue,
                    animationSpec = tween(durationMillis = 750)
                ).value,
                alpha = animateFloatAsState(
                    targetValue = alphaValue,
                    animationSpec = tween(durationMillis = 750)
                ).value
            ),
        onClick = onClick,
        contentPadding = PaddingValues(start = 24.dp,
            end = 24.dp,
            top = 8.dp,
            bottom = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = imageVector,
                contentDescription = textContent,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(text = textContent,
                style = typography.button.copy(letterSpacing = 0.5.sp))
        }
    }
}