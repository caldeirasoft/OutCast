package com.caldeirasoft.outcast.ui.screen.search_results

import android.accessibilityservice.AccessibilityService
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.data.db.entities.Episode
import com.caldeirasoft.outcast.data.db.entities.Podcast
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.screen.base.SearchUiModel
import com.caldeirasoft.outcast.ui.screen.base.StoreUiModel
import com.caldeirasoft.outcast.ui.screen.store.storedata.StoreCollectionItemsContent
import com.caldeirasoft.outcast.ui.screen.store.topcharts.pagerTabIndicatorOffset
import com.caldeirasoft.outcast.ui.theme.colors
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.*
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import cz.levinzonr.router.core.Route
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Route(
    name = "search_results",
)
@Composable
fun SearchResultsScreen(
    viewModel: SearchResultsViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val localPodcastsResults = viewModel.localSearchPodcastsResults.collectAsLazyPagingItems()
    val localEpisodesResults = viewModel.localSearchEpisodesResults.collectAsLazyPagingItems()
    val focusRequester = remember { FocusRequester.Default }
    var isSearchActive by rememberSaveable { mutableStateOf(true) }
    val searchQuery = rememberSaveable { mutableStateOf(state.query ?: "") }

    SearchResultsScreen(
        state = state,
        searchQuery = searchQuery,
        searchResults = searchResults,
        localPodcastsResults = localPodcastsResults,
        localEpisodesResults = localEpisodesResults,
        navigateToStore = { navController.navigateToStore(it) },
        navigateToStorePodcast = { navController.navigateToPodcast(it) },
        navigateToStoreEpisode = { navController.navigateToEpisode(it) },
        navigateToPodcast = { navController.navigateToPodcast(it) },
        navigateToEpisode = { navController.navigateToEpisode(it) },
        navigateUp = { navController.navigateUp() },
        onSearch = { query ->
            if (query.isNotEmpty()) {
                isSearchActive = false
                viewModel.search(query)
            }
        },
        onSearchHint = viewModel::searchHint,
        onToggleSearch = {
            isSearchActive = it
        },
        onFollowPodcast = { /*TODO*/ },
        focusRequester = focusRequester,
        isSearchActive = isSearchActive
    )
}

@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchResultsScreen(
    state: SearchResultsState,
    searchQuery: MutableState<String>,
    searchResults: LazyPagingItems<StoreUiModel>,
    localPodcastsResults: LazyPagingItems<Podcast>,
    localEpisodesResults: LazyPagingItems<Episode>,
    navigateToStore: (StoreData) -> Unit,
    navigateToStorePodcast: (StorePodcast) -> Unit,
    navigateToStoreEpisode: (StoreEpisode) -> Unit,
    navigateToPodcast: (Podcast) -> Unit,
    navigateToEpisode: (Episode) -> Unit,
    navigateUp: () -> Unit,
    onSearch: (String) -> Unit,
    onSearchHint: (String) -> Unit,
    onToggleSearch: (Boolean) -> Unit,
    onFollowPodcast: (StorePodcast) -> Unit,
    focusRequester: FocusRequester,
    isSearchActive: Boolean,
) {
    var isSearchFocused by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            TopAppBar(
                modifier = Modifier,
                title = {
                    TextField(
                        value = searchQuery.value,
                        onValueChange = {
                            searchQuery.value = it
                            onSearchHint(it)
                        },
                        modifier = Modifier
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                isSearchFocused = it.isFocused
                            }
                            .onKeyEvent {
                                when (it.key) {
                                    Key.Enter -> {
                                        onSearch(searchQuery.value)
                                        keyboardController?.hide()
                                        true
                                    }
                                    else -> false
                                }
                            },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_query),
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                onSearch(searchQuery.value)
                                keyboardController?.hide()
                            }
                        ),
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = MaterialTheme.colors.surface,
                            focusedIndicatorColor = MaterialTheme.colors.surface,
                            unfocusedIndicatorColor = MaterialTheme.colors.surface,
                        ),

                        readOnly = !isSearchActive
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isSearchActive) {
                                onToggleSearch(false)
                            } else {
                                navigateUp()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                actions = {
                    if (!isSearchActive) {
                        IconButton(
                            onClick = {
                                onToggleSearch(true)
                                focusRequester.requestFocus()
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                            )
                        }
                    } else if (searchQuery.value.isNotBlank()) {
                        IconButton(
                            onClick = {
                                searchQuery.value = ""
                            },
                        ) {
                            if (searchQuery.value.isNotEmpty()) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) {
        if (!isSearchActive)
        {
            SearchResultsTabs(
                state = state,
                searchResults = searchResults,
                localPodcastsResults = localPodcastsResults,
                localEpisodesResults = localEpisodesResults,
                navigateToStore = navigateToStore,
                navigateToStorePodcast = navigateToStorePodcast,
                navigateToStoreEpisode = navigateToStoreEpisode,
                navigateToPodcast = navigateToPodcast,
                navigateToEpisode = navigateToEpisode,
                onFollowPodcast = onFollowPodcast
            )
        }
        else {
            SearchHintsScreen(
                hints = state.hints,
                onSearch = {
                    searchQuery.value = it
                    onSearch(it)
                    keyboardController?.hide()
                },
                onSetQuery = {
                    searchQuery.value = it
                }
            )
        }
    }

    DisposableEffect(Unit) {
        //focusRequester.requestFocus()
        onDispose { }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchHintsScreen(
    hints: List<SearchUiModel>,
    onSearch: (String) -> Unit,
    onSetQuery: (String) -> Unit
)
{
    val scrollState = rememberScrollState()
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(scrollState)
    ) {
        hints.forEach { searchUiModel ->
            when (searchUiModel) {
                is SearchUiModel.HistoryItem ->
                    ListItem(
                        icon = {
                            IconButton(onClick = { onSearch(searchUiModel.item) }) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null
                                )
                            }
                        },
                        text = {
                            Text(
                                searchUiModel.item,
                                style = typography.body1,
                                modifier = Modifier.clickable { onSearch(searchUiModel.item) }
                            )
                        },
                        trailing = {
                            IconButton(onClick = { onSetQuery(searchUiModel.item) }) {
                                Icon(
                                    imageVector = Icons.Default.NorthWest,
                                    contentDescription = null
                                )
                            }
                        }
                    )
                is SearchUiModel.HintItem ->
                    ListItem(
                        icon = {
                            IconButton(onClick = { onSearch(searchUiModel.item) }) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null
                                )
                            }
                        },
                        text = {
                            Text(
                                searchUiModel.item,
                                style = typography.body1,
                                modifier = Modifier.clickable { onSearch(searchUiModel.item) }
                            )
                        },
                        trailing = {
                            IconButton(onClick = { onSetQuery(searchUiModel.item) }) {
                                Icon(
                                    imageVector = Icons.Default.NorthWest,
                                    contentDescription = null
                                )
                            }
                        }
                    )
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchResultsTabs(
    state: SearchResultsState,
    searchResults: LazyPagingItems<StoreUiModel>,
    localPodcastsResults: LazyPagingItems<Podcast>,
    localEpisodesResults: LazyPagingItems<Episode>,
    navigateToStore: (StoreData) -> Unit,
    navigateToStorePodcast: (StorePodcast) -> Unit,
    navigateToStoreEpisode: (StoreEpisode) -> Unit,
    navigateToPodcast: (Podcast) -> Unit,
    navigateToEpisode: (Episode) -> Unit,
    onFollowPodcast: (StorePodcast) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val titles = listOf(
        stringResource(R.string.tab_title_all_podcasts),
        stringResource(R.string.tab_title_library)
    )
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState(pageCount = titles.size)

        TabRow(
            backgroundColor = Color.Transparent,
            // Our selected tab is our current page
            selectedTabIndex = pagerState.currentPage,
            // Override the indicator, using the provided pagerTabIndicatorOffset modifier
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            // Add tabs for all of our pages
            titles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        // Animate to the selected page when clicked
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    unselectedContentColor = MaterialTheme
                        .colors
                        .onBackground
                        .copy(alpha = ContentAlpha.medium)
                )
            }
        }

        // pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (page) {
                0 -> { // all podcasts
                    SearchResultsAllPodcastTab(
                        state = state,
                        searchResults = searchResults,
                        navigateToStore = navigateToStore,
                        navigateToStorePodcast = navigateToStorePodcast,
                        navigateToStoreEpisode = navigateToStoreEpisode,
                        onFollowPodcast = onFollowPodcast
                    )
                }
                1 -> { // your library
                    SearchResultsYourLibraryTab(
                        state = state,
                        localPodcastsResults = localPodcastsResults,
                        localEpisodesResults = localEpisodesResults,
                        navigateToPodcast = navigateToPodcast,
                        navigateToEpisode = navigateToEpisode,
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultsAllPodcastTab(
    state: SearchResultsState,
    searchResults: LazyPagingItems<StoreUiModel>,
    navigateToStore: (StoreData) -> Unit,
    navigateToStorePodcast: (StorePodcast) -> Unit,
    navigateToStoreEpisode: (StoreEpisode) -> Unit,
    onFollowPodcast: (StorePodcast) -> Unit,
) {
    val listState = searchResults.rememberLazyListStateWithPagingItems()
    LazyColumn(
        state = listState,
        modifier = Modifier
    )
    {
        // podcasts + episodes
        searchResults
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
                items(lazyPagingItems = searchResults) { storeUiModel ->
                    when (storeUiModel) {
                        is StoreUiModel.TitleItem -> {
                            // header
                            when (val collection = storeUiModel.item) {
                                is StoreCollectionItems -> {
                                    StoreHeadingSection(
                                        title = getAllPodcastsSectionTitle(label = collection.label)
                                    )
                                }
                                is StoreCollectionEpisodes -> {
                                    StoreHeadingSection(
                                        title = getAllPodcastsSectionTitle(label = collection.label)
                                    )
                                }
                            }
                        }
                        is StoreUiModel.StoreUiItem -> {
                            // content
                            when (val storeItem = storeUiModel.item) {
                                is StoreCollectionItems -> {
                                    StoreCollectionItemsContent(
                                        storeCollection = storeItem,
                                        openStoreDataDetail = navigateToStore,
                                        openPodcastDetail = navigateToStorePodcast,
                                        openEpisodeDetail = navigateToStoreEpisode,
                                        followingStatus = state.followingStatus,
                                        followLoadingStatus = state.followLoadingStatus,
                                        onFollowPodcast = {
                                            onFollowPodcast(it)
                                        },
                                    )
                                }
                                is StoreEpisode -> {
                                    StoreEpisodeItem(
                                        episode = storeItem.episode,
                                        modifier = Modifier.fillMaxWidth(),
                                        onThumbnailClick = { navigateToStorePodcast(storeItem.storePodcast) },
                                        onEpisodeClick = { navigateToStoreEpisode(storeItem) },
                                        index = storeUiModel.index
                                    )
                                }
                            }
                        }
                    }
                }
            }
    }
}

@Composable
fun SearchResultsYourLibraryTab(
    state: SearchResultsState,
    localPodcastsResults: LazyPagingItems<Podcast>,
    localEpisodesResults: LazyPagingItems<Episode>,
    navigateToPodcast: (Podcast) -> Unit,
    navigateToEpisode: (Episode) -> Unit,
) {
    val listState = localEpisodesResults.rememberLazyListStateWithPagingItems()
    LazyColumn(
        state = listState,
        modifier = Modifier
    )
    {
        // podcasts
        localPodcastsResults
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
                item {
                    StoreHeadingSection(
                        title = stringResource(id = R.string.store_podcasts)
                    )
                }
                item {
                    val podcastListState = rememberLazyListState()
                    // content
                    LazyRow(
                        state = podcastListState,
                        modifier = Modifier.nestedScroll(podcastListState.nestedScrollConnection),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(lazyPagingItems = localPodcastsResults) { item ->
                            item?.let {
                                PodcastSearchGridItem(
                                    modifier = Modifier
                                        .width(150.dp),
                                    onClick = { navigateToPodcast(item) },
                                    podcast = item,
                                )
                            }
                        }
                    }
                }
            }

        // episodes
        localEpisodesResults
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
                item {
                    StoreHeadingSection(
                        title = stringResource(id = R.string.store_episodes)
                    )
                }
                items(lazyPagingItems = localEpisodesResults) { episode ->
                    episode?.let {
                        EpisodeItem(
                            episode = episode,
                            modifier = Modifier.fillMaxWidth(),
                            onPodcastClick = null,
                            onEpisodeClick = { navigateToEpisode(episode) },
                        )
                    }
                }
            }
    }
}

@Composable
private fun getAllPodcastsSectionTitle(label: String): String
{
    return when(label) {
        "podcast" -> stringResource(id = R.string.store_podcasts)
        "podcastEpisode" -> stringResource(id = R.string.store_episodes)
        else -> ""
    }
}

@Composable
private fun PodcastSearchGridItem(
    modifier: Modifier = Modifier,
    podcast: Podcast,
    onClick: () -> Unit,
) {
    Column(modifier = modifier) {
        Card(
            backgroundColor = colors[1],
            shape = RoundedCornerShape(8.dp),
            onClick = onClick
        )
        {
            Image(
                painter = rememberCoilPainter(request = podcast.artworkUrl),
                contentDescription = podcast.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f))
        }
        Text(
            text = podcast.name,
            modifier = Modifier.fillMaxWidth(),
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.body1
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Text(
                text = podcast.artistName,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.body1
            )
        }
    }
}
