package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.ChipButton
import com.caldeirasoft.outcast.ui.components.LazyListLayout
import com.caldeirasoft.outcast.ui.components.PodcastListItem
import com.caldeirasoft.outcast.ui.components.StoreEpisodeItem
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetContent
import com.caldeirasoft.outcast.ui.components.bottomsheet.LocalBottomSheetState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.episode.EpisodeArg.Companion.toEpisodeArg
import com.caldeirasoft.outcast.ui.screen.store.categories.CategoriesListBottomSheet
import com.caldeirasoft.outcast.ui.util.ifLoadingMore
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch


@Composable
fun TopChartPodcastScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: TopChartSectionViewModel = mavericksViewModel(
        initialArgument = StoreItemType.PODCAST,
        keyFactory = { StoreItemType.PODCAST.name })
    TopChartSectionScreen(viewModel = viewModel, navigateTo = navigateTo)
}

@Composable
fun TopChartEpisodeScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: TopChartSectionViewModel = mavericksViewModel(
        initialArgument = StoreItemType.EPISODE,
        keyFactory = { StoreItemType.EPISODE.name })
    TopChartSectionScreen(viewModel = viewModel, navigateTo = navigateTo)
}

@OptIn(FlowPreview::class)
@Composable
private fun TopChartSectionScreen(
    viewModel: TopChartSectionViewModel,
    navigateTo: (Screen) -> Unit,
) {
    val state by viewModel.collectAsState()
    val lazyPagingItems = viewModel.topCharts.collectAsLazyPagingItems()

    LazyListLayout(lazyListItems = lazyPagingItems) {
        val listState = rememberLazyListState(0)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()) {

            itemsIndexed(lazyPagingItems = lazyPagingItems) { index, item ->
                when (item) {
                    is StorePodcast -> {
                        PodcastListItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    navigateTo(Screen.PodcastScreen(item))
                                }),
                            storePodcast = item,
                            index = index + 1,
                            isFollowing = state.followingStatus.contains(item.id),
                            isFollowingLoading = state.followLoadingStatus.contains(item.id),
                            onSubscribeClick = viewModel::followPodcast
                        )
                        Divider()
                    }
                    is StoreEpisode -> {
                        StoreEpisodeItem(
                            episode = item.episode,
                            onEpisodeClick = { navigateTo(Screen.EpisodeScreen(item.toEpisodeArg())) },
                            onPodcastClick = { navigateTo(Screen.PodcastScreen(item.podcast)) },
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
    }
}

//private val emptyTabIndicator: @Composable (List<TabPosition>) -> Unit = {}
