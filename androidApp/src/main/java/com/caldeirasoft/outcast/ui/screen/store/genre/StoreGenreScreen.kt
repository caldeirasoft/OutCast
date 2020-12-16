package com.caldeirasoft.outcast.ui.screen.store

import android.util.Log
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollableRow
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.viewModel
import androidx.paging.PagingData
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.store.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.DiscoverContent
import com.caldeirasoft.outcast.ui.components.ErrorScreen
import com.caldeirasoft.outcast.ui.components.LoadingScreen
import com.caldeirasoft.outcast.ui.components.StoreChartTab
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.store.base.StoreRoomBaseViewModel
import com.caldeirasoft.outcast.ui.screen.store.directory.StoreDirectoryViewModel
import com.caldeirasoft.outcast.ui.screen.store.genre.StoreGenreViewModel
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartContent
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopChartViewModel
import com.caldeirasoft.outcast.ui.util.*
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel

typealias StoreGenreState = StoreGenreViewModel.State

private enum class StoreGenreTab(
    val titleId: Int,
) {
    Discover(R.string.store_tab_discover),
    Charts(R.string.store_tab_charts),
}

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreGenreScreen(storeGenre: StoreGenre) {
    val actions = ActionsAmbient.current
    val viewModel: StoreGenreViewModel = viewModel(
        key = "store_genre_${storeGenre.id}",
        factory = viewModelProviderFactoryOf { StoreGenreViewModel(storeGenre) }
    )
    val viewState by viewModel.state.collectAsState()
    var selectedTab: StoreGenreTab by remember { mutableStateOf(StoreGenreTab.Discover) }
    var selectedChartTab: StoreChartTab by remember { mutableStateOf(StoreChartTab.Podcasts) }

    Log.d("Compose", "Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreGenreContent(
        storeGenre = storeGenre,
        state = viewState,
        discover = viewModel.storeDataPagedList,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        selectedChartTab = selectedChartTab,
        onChartSelected = { selectedChartTab = it },
        actions = actions
    )
}

@Composable
private fun StoreGenreContent(
    storeGenre: StoreGenre,
    state: StoreGenreState,
    discover: Flow<PagingData<StoreItem>>,
    selectedTab: StoreGenreTab,
    onTabSelected: (StoreGenreTab) -> Unit,
    selectedChartTab: StoreChartTab,
    onChartSelected: (StoreChartTab) -> Unit,
    actions: Actions
) {
    Log.d("Compose", "Compose StoreTabLayout : ${Clock.System.now()}")
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = storeGenre.name) },
                navigationIcon = {
                    IconButton(onClick = actions.navigateUp) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                backgroundColor = Color.Transparent
            )
        }
    )
    {
        Column {
            Tabs(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected)

            Surface(modifier = Modifier.weight(0.5f)) {
                when (selectedTab) {
                    StoreGenreTab.Discover -> DiscoverContent(
                        state = state.storeState,
                        discover = discover,
                        actions = actions
                    )
                    StoreGenreTab.Charts -> TopChartsTabContent(
                        state = state.chartState,
                        selectedChartTab = selectedChartTab,
                        onChartSelected = onChartSelected,
                        storePage = state.chartData
                    )
                }
            }
        }
    }
}

@Composable
private fun Tabs(
    selectedTab: StoreGenreTab,
    onTabSelected: (StoreGenreTab) -> Unit,
) {
    TabRow(
        selectedTabIndex = selectedTab.ordinal,
        backgroundColor = Color.Transparent
    )
    {
        StoreGenreTab.values().forEachIndexed { index, tab ->
            Tab(
                selected = (index == selectedTab.ordinal),
                onClick = { onTabSelected(tab) },
                text = {
                    Text(
                        text = stringResource(id = tab.titleId),
                        style = MaterialTheme.typography.body2
                    )
                }
            )
        }
    }
}

@Composable
private fun TopChartsTabContent (
    state: ScreenState,
    selectedChartTab: StoreChartTab,
    onChartSelected: (StoreChartTab) -> Unit,
    storePage: StoreTopCharts?,
) {
    state
        .onLoading { LoadingScreen() }
        .onError { ErrorScreen(t = it) }
        .onSuccess {
            storePage?.let { storePage ->
                Column {
                    TabRow(
                        selectedTabIndex = selectedChartTab.ordinal,
                        backgroundColor = Color.Transparent
                    )
                    {
                        StoreChartTab.values().forEachIndexed { index, tab ->
                            Tab(
                                selected = (index == selectedChartTab.ordinal),
                                onClick = { onChartSelected(tab) },
                                text = {
                                    Text(
                                        text = selectedChartTab.name,
                                        style = MaterialTheme.typography.body2
                                    )
                                }
                            )
                        }
                    }
                    Surface(modifier = Modifier.weight(0.5f)) {
                        when (selectedChartTab) {
                            StoreChartTab.Podcasts ->
                                TopChartContent(
                                    topChart = storePage.topPodcastsChart,
                                    storePage = storePage
                                )
                            StoreChartTab.Episodes ->
                                TopChartContent(
                                    topChart = storePage.topEpisodesChart,
                                    storePage = storePage
                                )
                        }
                    }
                }
            }
        }
}
