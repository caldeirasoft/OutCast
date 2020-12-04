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
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.storedirectory.DiscoverViewModel
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
    val discover = viewModel.discover
    val pagedList = discover.collectAsLazyPagingItems()

    DiscoverContent(
        discoverPagedList = pagedList,
        actions = actions
    )
}

@Composable
fun DiscoverContent(
    discoverPagedList: LazyPagingItems<StoreItem>,
    actions: Actions
)
{
    StoreContentFeed(
        lazyPagingItems = discoverPagedList,
        actions = actions) { item, index ->
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