package com.caldeirasoft.outcast.ui.screen.store.topchartsection

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.util.*

@Composable
fun TopChartPodcastScreen(
    navigateTo: (Screen) -> Unit,
) {
    val viewModel: TopChartSectionViewModel = mavericksViewModel(
        initialArgument = StoreItemType.PODCAST,
        keyFactory = { StoreItemType.PODCAST.name })

    val state by viewModel.collectAsState()
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
                    TopChartPodcastLoadingScreen()
                }
            }
            .ifError {
                item {
                    ErrorScreen(t = it)
                }
            }
            .ifNotLoading {
                gridItemsIndexed(
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = PaddingValues(16.dp),
                    horizontalInnerPadding = 16.dp,
                    verticalInnerPadding = 16.dp,
                    columns = 2) { index, item ->
                    when (item) {
                        is StorePodcast -> {
                            PodcastGridItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        navigateTo(Screen.PodcastScreen(item))
                                    }),
                                podcast = item,
                                index = index + 1,
                                isFollowing = state.followingStatus.contains(item.id),
                                isFollowingLoading = state.followLoadingStatus.contains(item.id),
                                onFollowPodcast = viewModel::followPodcast
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
fun TopChartPodcastLoadingScreen() {
    LoadingListShimmer { list, floatAnim ->
        val brush = Brush.verticalGradient(list, 0f, floatAnim)
        Column() {
            Grid(
                items = (0..12).toList(),
                contentPadding = PaddingValues(16.dp),
                mainAxisSpacing = 8.dp,
                crossAxisSpacing = 8.dp,
                columns = 2
            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .background(brush = brush)
                )
            }
        }
    }
}
