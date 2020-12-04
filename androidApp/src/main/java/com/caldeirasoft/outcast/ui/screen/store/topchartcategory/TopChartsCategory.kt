package com.caldeirasoft.outcast.ui.screen.store.topchartcategory

import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsCategory(
    type: TopChartsType,
) {
    val viewModel = getViewModel<TopChartsCategoryViewModel> { parametersOf(type) }
    val actions = ActionsAmbient.current
    val topCharts = viewModel.topCharts
    val topChartsItems = topCharts.collectAsLazyPagingItems()

    println("Compose TopChartsCategory : ${Clock.System.now()}")

    StoreContentFeed(
        lazyPagingItems = topChartsItems,
        actions = actions) { item, index ->
        when (item) {
            is StorePodcast -> {
                StorePodcastListItemIndexed(podcast = item, index = index + 1)
                Divider()
            }
        }
    }
}