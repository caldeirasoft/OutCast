package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientDensity
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.AmbientBottomDrawerContent
import com.caldeirasoft.outcast.ui.navigation.AmbientBottomDrawerState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.openEpisodeDialog
import com.caldeirasoft.outcast.ui.screen.store.categories.CategoriesListBottomSheet
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreGenreItem
import com.caldeirasoft.outcast.ui.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow


@ExperimentalMaterialApi
@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    storeItemType: StoreItemType = StoreItemType.PODCAST,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: TopChartsViewModel = viewModel(
        key = "store_chart",
        factory = viewModelProviderFactoryOf { TopChartsViewModel(storeItemType) }
    )
    val viewState by viewModel.state.collectAsState()
    TopChartsScreen(
        viewState = viewState,
        topCharts = viewModel.topCharts,
        onChartTabSelected = viewModel::onTabSelected,
        onChartsGenreSelected = viewModel::onGenreSelected,
        navigateTo = navigateTo,
        navigateBack = navigateBack
    )
}

@ExperimentalMaterialApi
@OptIn(ExperimentalAnimationApi::class)
@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    viewState: TopChartsViewModel.State,
    topCharts: Flow<PagingData<StoreItem>>,
    onChartTabSelected: (StoreItemType) -> Unit,
    onChartsGenreSelected: (Int?) -> Unit,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val selectedGenre = viewState.selectedGenre
    val drawerState = AmbientBottomDrawerState.current
    val drawerContent = AmbientBottomDrawerContent.current
    val lazyPagingItems = topCharts.collectAsLazyPagingItems()
    val listState = rememberLazyListState(0)

    ReachableScaffold { headerHeight ->
        val spacerHeight = headerHeight - 56.px

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)) {

            item {
                with(LocalDensity.current) {
                    Spacer(modifier = Modifier.height(spacerHeight.toDp()))
                }
            }

            item {
                ScrollableRow(contentPadding = PaddingValues(start = 16.dp, end = 16.dp)) {
                    ChipRadioSelector(
                        selectedValue = viewState.selectedChartTab,
                        values = StoreItemType.values(),
                        onClick = onChartTabSelected,
                        text = {
                            Text(text = stringResource(id = when (it) {
                                StoreItemType.PODCAST -> R.string.store_podcasts
                                StoreItemType.EPISODE -> R.string.store_episodes
                            }))
                        })

                    Spacer(modifier = Modifier.width(16.dp))

                    ChipButton(
                        selected = (selectedGenre != null),
                        onClick = {
                            drawerContent.updateContent {
                                CategoriesListBottomSheet(
                                    selectedGenre = selectedGenre,
                                    onGenreSelected = onChartsGenreSelected
                                )
                            }
                            drawerState.show()
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

            lazyPagingItems
                .ifLoading {
                    item {
                        ShimmerStorePodcastList()
                    }
                }
                .ifError {
                    item {
                        ErrorScreen(t = it)
                    }
                }
                .ifNotLoading {
                    itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
                        when (item) {
                            is StorePodcast -> {
                                PodcastListItemIndexed(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = {
                                            navigateTo(Screen.StorePodcastScreen(item))
                                        }),
                                    storePodcast = item,
                                    index = index + 1
                                )
                                Divider()
                            }
                            is StoreEpisode -> {
                                EpisodeItemWithArtwork(
                                    onEpisodeClick = { openEpisodeDialog(drawerState, drawerContent, item) },
                                    onPodcastClick = { navigateTo(Screen.StorePodcastScreen(item.podcast)) },
                                    storeEpisode = item,
                                    index = index + 1
                                )
                                Divider()
                            }
                        }
                    }
                }
        }

        ReachableAppBar(
            title = { Text(text = stringResource(id = R.string.store_tab_charts)) },
            navigationIcon = {
                IconButton(onClick = navigateBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "back")
                }
            },
            actions = {
                IconButton(onClick = { }) {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "search")
                }
            },
            state = listState,
            headerHeight = headerHeight)
    }
}

private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}
