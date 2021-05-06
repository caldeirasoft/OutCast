package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.episode
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.*


@Composable
fun TopChartEpisodeScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: TopChartSectionViewModel = hiltNavGraphViewModel()

    val lazyPagingItems = viewModel.topCharts.collectAsLazyPagingItems()
    val listState = rememberLazyListState(0)

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
    ) {
        lazyPagingItems
            .ifLoading {
                item {
                    TopChartEpisodeLoadingScreen()
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
                        is StoreEpisode -> {
                            StoreEpisodeItem(
                                episode = item.episode,
                                onEpisodeClick = { navigateTo(Screen.EpisodeScreen(item)) },
                                onThumbnailClick = { navigateTo(Screen.PodcastScreen(item.storePodcast)) },
                                onContextMenuClick = { },
                                index = index + 1
                            )
                            Divider()
                        }
                    }
                }
            }
            .ifLoadingMore {
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
}

@Composable
fun TopChartEpisodeLoadingScreen() {
    LoadingListShimmer { list, floatAnim ->
        val brush = Brush.verticalGradient(list, 0f, floatAnim)
        Column(modifier = Modifier
            .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)

        ) {
            // episodes
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                repeat(12) {
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
                                repeat(2) {
                                    Spacer(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(10.dp)
                                        .background(brush = brush))
                                }
                            }
                        },
                        icon = {
                            Spacer(modifier = Modifier
                                .size(56.dp)
                                .background(brush = brush))
                        }
                    )
                }
            }
        }
    }
}
