package com.caldeirasoft.outcast.ui.screen.store.storeroom

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onError
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onLoading
import com.caldeirasoft.outcast.domain.util.Resource.Companion.onSuccess
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.util.viewModelProviderFactoryOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
@Composable
fun StoreRoomScreen(
    storeRoom: StoreRoom,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit
)
{
    val viewModel: StoreRoomViewModel = viewModel(
        key = "store_room_${storeRoom.id}",
        factory = viewModelProviderFactoryOf { StoreRoomViewModel(storeRoom) }
    )
    val viewState by viewModel.state.collectAsState()
    StoreRoomScreen(
        title = storeRoom.label,
        viewState = viewState,
        discover = viewModel.discover,
        navigateToRoom = navigateToRoom,
        navigateToPodcast = navigateToPodcast,
        navigateUp = navigateUp)
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreRoomScreen(
    title: String,
    viewState: StoreRoomViewModel.State,
    discover: Flow<PagingData<StoreItem>>,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
    navigateUp: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = title)
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                actions = {
                    IconButton(onClick = navigateUp) {
                        Icon(imageVector = Icons.Filled.HourglassFull)
                    }
                })
        }
    )
    {
        viewState
            .storeResourceData
            .onLoading { LoadingListShimmer() }
            .onError { ErrorScreen(t = it) }
            .onSuccess<StoreRoomPage> {
                StoreRoomPodcastContent(
                    storeData = it,
                    discover = discover,
                    navigateToPodcast = navigateToPodcast, )
            }
            .onSuccess<StoreMultiRoomPage> {
                StoreMutiRoomContent(
                    storeData = it,
                    discover = discover,
                    navigateToRoom = navigateToRoom,
                    navigateToPodcast = navigateToPodcast)
            }
    }
}

@Composable
private fun StoreRoomPodcastContent(
    storeData: StoreRoomPage,
    discover: Flow<PagingData<StoreItem>>,
    navigateToPodcast: (String) -> Unit,
) {
    val lazyPagingItems = discover.collectAsLazyPagingItems()
    val loadState = lazyPagingItems.loadState
    val refreshState = loadState.refresh
    val appendState = loadState.append
    LazyColumn {
        // content
        when {
            refreshState is LoadState.Loading -> {
                item {
                    LoadingListShimmer()
                }
            }
            refreshState is LoadState.Error -> {
                item {
                    ErrorScreen(t = refreshState.error)
                }
            }
            refreshState is LoadState.NotLoading -> {
                gridItems(
                    lazyPagingItems = lazyPagingItems,
                    contentPadding = PaddingValues(16.dp),
                    horizontalInnerPadding = 8.dp,
                    verticalInnerPadding = 8.dp,
                    columns = 3
                ) { item ->
                    item?.let {
                        when (item) {
                            is StorePodcast -> {
                                PodcastGridItem(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = { navigateToPodcast(item.url) }),
                                    podcast = item)
                            }
                        }
                    }
                }
            }
        }

        when (appendState) {
            is LoadState.Loading -> {
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
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun StoreMutiRoomContent(
    storeData: StoreMultiRoomPage,
    discover: Flow<PagingData<StoreItem>>,
    navigateToRoom: (StoreRoom) -> Unit,
    navigateToPodcast: (String) -> Unit,
) {
    DiscoverContent(
        discover = discover,
    ) { _, item ->
        when (item) {
            is StoreCollectionPodcasts ->
                StoreCollectionPodcastsContent(
                    storeCollection = item,
                    navigateToRoom = navigateToRoom,
                    navigateToPodcast = navigateToPodcast,
                )
            is StoreCollectionEpisodes ->
                StoreCollectionEpisodesContent(
                    storeCollection = item
                )
            is StoreCollectionRooms ->
                StoreCollectionRoomsContent(
                    storeCollection = item,
                    navigateToRoom = navigateToRoom
                )
        }
    }
}
