package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.compose.collectAsState
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enum.StoreItemType
import com.caldeirasoft.outcast.ui.components.foundation.LocalViewPagerController
import com.caldeirasoft.outcast.ui.components.foundation.ViewPager
import com.caldeirasoft.outcast.ui.components.foundation.ViewPagerController
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.store.topchartsection.TopChartEpisodeScreen
import com.caldeirasoft.outcast.ui.screen.store.topchartsection.TopChartPodcastScreen
import com.caldeirasoft.outcast.ui.util.mavericksViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview


@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopChartsScreen(
    storeItemType: StoreItemType = StoreItemType.PODCAST,
    navigateTo: (Screen) -> Unit,
    navigateBack: () -> Unit,
) {
    val viewModel: TopChartsViewModel = mavericksViewModel(initialArgument = storeItemType)
    val state by viewModel.collectAsState()
    val viewPagerController = remember { ViewPagerController() }
    val selectedChartTab = remember { state.selectedChartTab }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.store_tab_charts))
                },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    }
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp
            )
        }
    ) {
        Column {
            TopChartsTabRow(
                selectedChartTab = state.selectedChartTab,
                onChartSelected = viewModel::onTabSelected,
                pagerController = viewPagerController
            )
            Surface(modifier = Modifier
                .weight(1f)
            ) {
                CompositionLocalProvider(LocalViewPagerController provides viewPagerController) {
                    TopChartsTabContent(
                        selectedChartTab = selectedChartTab,
                        onChartSelected = viewModel::onTabSelected,
                        navigateTo = navigateTo,
                    )
                }
            }
        }
    }
}

@Composable
private fun TopChartsTabRow(
    selectedChartTab: StoreItemType,
    onChartSelected: (StoreItemType) -> Unit,
    pagerController: ViewPagerController,
) {
    TabRow(
        selectedTabIndex = selectedChartTab.ordinal,
        backgroundColor = Color.Transparent
    )
    {
        StoreItemType.values().forEachIndexed { index, tab ->
            Tab(
                selected = (index == selectedChartTab.ordinal),
                onClick = {
                    onChartSelected(tab)
                    pagerController.moveTo(tab.ordinal)
                },
                text = {
                    Text(
                        text = stringResource(id = when (tab) {
                            StoreItemType.PODCAST -> R.string.store_podcasts
                            StoreItemType.EPISODE -> R.string.store_episodes
                        }),
                        style = MaterialTheme.typography.body2)
                }
            )
        }
    }
}


@Composable
private fun TopChartsTabContent(
    selectedChartTab: StoreItemType,
    onChartSelected: (StoreItemType) -> Unit,
    navigateTo: (Screen) -> Unit,
) {
    ViewPager(
        modifier = Modifier.fillMaxSize(),
        range = 0..1,
        initialPage = selectedChartTab.ordinal,
        onPageChanged = { onChartSelected(StoreItemType.values()[Math.floorMod(it, 2)]) }
    ) {
        val page = Math.floorMod(this.index, 2)
        val itemType = StoreItemType.values()[page]
        Box(modifier = Modifier.fillMaxSize()) {
            when (itemType) {
                StoreItemType.PODCAST -> TopChartPodcastScreen(navigateTo = navigateTo)
                StoreItemType.EPISODE -> TopChartEpisodeScreen(navigateTo = navigateTo)
            }
        }
    }
}

