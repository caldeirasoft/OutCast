package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.categories.CategoriesListBottomSheet
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreGenreItem
import com.caldeirasoft.outcast.ui.util.ifLoadingMore
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import com.caldeirasoft.outcast.ui.util.px
import com.caldeirasoft.outcast.ui.util.toDp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    storeItemType: StoreItemType = StoreItemType.PODCAST,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: TopChartsViewModel = mavericksViewModel(initialArgument = storeItemType)
    val state by viewModel.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val selectedGenre = state.selectedGenre
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val lazyPagingItems = viewModel.topCharts.collectAsLazyPagingItems()

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyListLayout(lazyListItems = lazyPagingItems) {
            val listState = rememberLazyListState(0)

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp)) {

                item {
                    Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                }

                item {
                    LazyRow(contentPadding = PaddingValues(start = 16.dp, end = 16.dp)) {
                        item {
                            ChipRadioSelector(
                                selectedValue = state.selectedChartTab,
                                values = StoreItemType.values(),
                                onClick = viewModel::onTabSelected,
                                text = {
                                    Text(text = stringResource(id = when (it) {
                                        StoreItemType.PODCAST -> R.string.store_podcasts
                                        StoreItemType.EPISODE -> R.string.store_episodes
                                    }))
                                })
                        }

                        item {
                            Spacer(modifier = Modifier.width(16.dp))
                        }

                        item {
                            ChipButton(
                                selected = (selectedGenre != null),
                                onClick = {
                                    drawerContent.updateContent {
                                        CategoriesListBottomSheet(
                                            selectedGenre = selectedGenre,
                                            onGenreSelected = viewModel::onGenreSelected
                                        )
                                    }
                                    coroutineScope.launch {
                                        drawerState.show()
                                    }
                                })
                            {
                                Text(
                                    text = when (selectedGenre) {
                                        null -> stringResource(id = R.string.store_tab_categories)
                                        else -> stringResource(id = StoreGenreItem.values()
                                            .first { it.genreId == selectedGenre }.titleId)
                                    }
                                )
                            }
                        }
                    }
                }

                itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
                    when (item) {
                        is StorePodcast -> {
                            PodcastListItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        navigateTo(Screen.StorePodcastScreen(item))
                                    }),
                                storePodcast = item,
                                index = index + 1,
                                followingStatus = state.followingStatus[item.id],
                                onSubscribeClick = viewModel::subscribeToPodcast
                            )
                            Divider()
                        }
                        is StoreEpisode -> {
                            StoreEpisodeItem(
                                episode = item.episode,
                                onEpisodeClick = { navigateTo(Screen.EpisodeScreen(item.toEpisodeArg())) },
                                onPodcastClick = { navigateTo(Screen.StorePodcastScreen(item.podcast)) },
                                index = index + 1
                            )
                            Divider()
                        }
                    }
                }
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

            ReachableAppBar(
                title = { Text(text = stringResource(id = R.string.store_tab_charts)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = null,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                        )
                    }
                },
                state = listState,
                headerHeight = headerHeight)
        }
    }
}

//private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}
