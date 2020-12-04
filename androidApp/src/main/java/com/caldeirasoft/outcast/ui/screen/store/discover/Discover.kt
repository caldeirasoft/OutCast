package com.caldeirasoft.outcast.ui.screen.store.discover

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.domain.util.onError
import com.caldeirasoft.outcast.domain.util.onLoading
import com.caldeirasoft.outcast.domain.util.onSuccess
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.storedirectory.DiscoverViewModel
import com.caldeirasoft.outcast.ui.util.ScreenState
import com.caldeirasoft.outcast.ui.util.onError
import com.caldeirasoft.outcast.ui.util.onLoading
import com.caldeirasoft.outcast.ui.util.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel

typealias NavigateToStoreEntryCallBack = (String) -> Unit

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun Discover(viewModel: DiscoverViewModel = getViewModel()) {
    val actions = ActionsAmbient.current
    val viewState by viewModel.state.collectAsState()
    val pagedList = viewModel.discover.collectAsLazyPagingItems()

    DiscoverContent(
        state = viewState.screenState,
        discoverPagedList = pagedList,
        actions = actions
    )
}

@Composable
fun DiscoverContent(
    state: ScreenState,
    discoverPagedList: LazyPagingItems<StoreItem>,
    actions: Actions)
{
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            StoreContentFeed(
                lazyPagingItems = discoverPagedList,
                actions = actions
            ) { item, index ->
                when (item) {
                    is StorePodcast -> {
                        StorePodcastListItem(podcast = item)
                        Divider()
                    }
                    is StoreCollectionPodcasts ->
                        StoreCollectionPodcastsContent(storeCollection = item)
                    is StoreCollectionEpisodes ->
                        StoreCollectionEpisodesContent(storeCollection = item)
                    is StoreCollectionRooms ->
                        StoreCollectionRoomsContent(storeCollection = item)
                    is StoreCollectionFeatured ->
                        StoreCollectionFeaturedContent(storeCollection = item)
                }
            }
        }
}