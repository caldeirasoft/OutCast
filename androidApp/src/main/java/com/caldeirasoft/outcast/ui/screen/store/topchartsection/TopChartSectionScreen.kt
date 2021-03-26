package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.ChipButton
import com.caldeirasoft.outcast.ui.components.PodcastListItem
import com.caldeirasoft.outcast.ui.components.StoreEpisodeItem
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.categories.CategoriesListBottomSheet
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreGenreItem
import com.caldeirasoft.outcast.ui.util.ifLoadingMore
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@Composable
fun TopChartPodcastScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: TopChartPodcastViewModel = mavericksViewModel()
    TopChartSectionScreen(viewModel = viewModel, navigateTo = navigateTo)
}

@Composable
fun TopChartEpisodeScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: TopChartEpisodeViewModel = mavericksViewModel()
    TopChartSectionScreen(viewModel = viewModel, navigateTo = navigateTo)
}

@OptIn(FlowPreview::class)
@Composable
private fun TopChartSectionScreen(
    viewModel: TopChartSectionViewModel,
    navigateTo: (Screen) -> Unit,
) {
    val state by viewModel.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val selectedGenre = state.selectedGenre
    val drawerState = LocalBottomSheetState.current
    val drawerContent = LocalBottomSheetContent.current
    val lazyPagingItems = viewModel.topCharts.collectAsLazyPagingItems()

    //LazyListLayout(lazyListItems = lazyPagingItems) {
        val listState = rememberLazyListState(0)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            item {
                LazyRow(contentPadding = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp)) {
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
            /*
            item {
                FilterChipGroup(
                    modifier = Modifier.height(48.dp),
                    selectedValue = StoreGenreItem.values().firstOrNull { it.genreId == selectedGenre },
                    values = StoreGenreItem.values(),
                    onClick = { viewModel.onGenreSelected(it?.genreId) }) {
                    Text(
                        text = stringResource(id = it.titleId),
                        maxLines = 1
                    )
                }
            }
            */

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
            item {
                // bottom app bar spacer
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    //}
}

//private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}
