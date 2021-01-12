package com.caldeirasoft.outcast.ui.screen.store

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientAnimationClock
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.StoreEpisode
import com.caldeirasoft.outcast.domain.models.store.StorePodcast
import com.caldeirasoft.outcast.domain.models.store.StoreRoom
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onError
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

private val AmbientDominantColor = ambientOf<MutableState<Color>> { error("No dominant color") }

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreSearchScreen(
    navigateToGenre: (Int, String) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    val viewModel: StoreDirectoryViewModel = viewModel()
    val viewState by viewModel.state.collectAsState()

    Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreDirectoryContent(
        viewState = viewState,
        discover = viewModel.discover,
        navigateToGenre = navigateToGenre,
        navigateToRoom = navigateToRoom,
        navigateToTopCharts = navigateToTopCharts,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp,
    )
}

@Composable
private fun StoreDirectoryContent(
    viewState: StoreDirectoryViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateToGenre: (Int, String) -> Unit,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // search button
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = MaterialTheme.colors.onSurface
                                .copy(alpha = ContentAlpha.medium),
                            disabledContentColor = MaterialTheme.colors.onSurface
                                .copy(alpha = ContentAlpha.disabled)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.Search)
                            Text("Search", modifier = Modifier.padding(horizontal = 4.dp))
                        }
                    }
                },
                actions = {
                    /*IconButton(onClick = onRefresh) {
                        Icon(imageVector = Icons.Filled.Refresh)
                    }*/
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    )
    {
        StoreCollectionChartsContent(
            viewState = viewState,
            navigateToTopCharts = navigateToTopCharts,
            navigateToPodcast = navigateToPodcast
        )
    }
}

@Composable
private fun StoreCollectionChartsContent(
    viewState: StoreDirectoryViewModel.State,
    navigateToTopCharts: (Int, StoreItemType) -> Unit,
    navigateToPodcast: (String) -> Unit,
) {
    viewState
        .storeResourceData
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            /*
            LazyColumn {
                val storeCollectionCharts = viewState.storeCollectionCharts
                storeCollectionCharts.forEach { collection ->
                    item {
                        StoreHeadingSectionWithLink(
                            title = collection.label,
                            onClick = {
                                navigateToTopCharts(
                                    collection.genreId,
                                    collection.itemType
                                )
                            }
                        )
                    }
                    item {
                        StoreCollectionChartsContent(
                            collection.storeList,
                            navigateToPodcast = navigateToPodcast,
                        )
                    }
                }
            }*/
        }
}

@Composable
private fun LazyItemScope.StoreCollectionChartsContent(
    storeList: List<StoreItem>,
    navigateToPodcast: (String) -> Unit,
) {
    val chunkedItems = storeList.chunked(4)
    val pagerState: PagerState = run {
        val clock = AmbientAnimationClock.current
        remember(clock) { PagerState(clock, 0, 0, chunkedItems.size - 1) }
    }
    val selectedPage = remember { mutableStateOf(0) }


    Pager(
        state = pagerState,
        offscreenLimit = 2,
        contentAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .preferredHeight(288.dp)
    )
    {
        val chartItems = chunkedItems[page]
        selectedPage.value = pagerState.currentPage

        Column(
            modifier = Modifier
                .fillMaxWidth(0.90f)
                .padding(horizontal = 4.dp)
        )
        {
            chartItems.forEachIndexed { index, storeItem ->
                when (storeItem) {
                    is StorePodcast -> {
                        SmallPodcastListItemIndexed(
                            modifier = Modifier.fillMaxWidth()
                                .clickable(onClick = { navigateToPodcast(storeItem.url) }),
                            storePodcast = storeItem,
                            index = index + 1)
                    }
                    is StoreEpisode -> {
                        StoreEpisodeSmallListItemIndexed(
                            episode = storeItem,
                            index = 4 * pagerState.currentPage + index + 1,
                            navigateToEpisode = { }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.preferredHeight(8.dp))
        }
    }
}
