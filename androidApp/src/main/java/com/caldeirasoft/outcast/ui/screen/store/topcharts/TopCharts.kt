package com.caldeirasoft.outcast.ui.screen.store.topcharts

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.store.topchartcategory.TopChartsCategory
import com.caldeirasoft.outcast.ui.screen.store.topchartcategory.TopChartsType
import com.caldeirasoft.outcast.ui.screen.storedirectory.TopChartsViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel

typealias NavigateToStoreEntryCallBack = (String) -> Unit

@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun TopCharts(viewModel: TopChartsViewModel = getViewModel()) {
    val actions = ActionsAmbient.current
    val viewState by viewModel.state.collectAsState()

    println("Compose StoreDirectoryScreen : ${Clock.System.now()}")

    TopChartsTabLayout(
        tabs = viewState.topChartsTab,
        selectedTab = viewState.selectedTopChartsTab,
        onTabSelected = viewModel::onTopChartsTabSelected,
        actions = actions
    )
}

@Composable
fun TopChartsTabLayout(
    tabs: List<TopChartsTab>,
    selectedTab: TopChartsTab,
    onTabSelected: (TopChartsTab) -> Unit,
    actions: Actions
) {
    val selectedTabIndex = tabs.indexOfFirst { it == selectedTab }


    Column {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color.Transparent
        )
        {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = (index == selectedTabIndex),
                    onClick = { onTabSelected(tab) },
                    text = {
                        Text(
                            text = when (tab) {
                                TopChartsTab.PODCASTS -> "Podcasts"
                                TopChartsTab.EPISODES -> "Episodes"
                            },
                            style = MaterialTheme.typography.body2
                        )
                    }
                )
            }
        }
        Surface(modifier = Modifier.weight(0.5f)) {
            when (selectedTabIndex) {
                TopChartsTab.PODCASTS.ordinal -> TopChartsCategory(type = TopChartsType(podcast = true, podcastEpisode = false))
                TopChartsTab.EPISODES.ordinal -> TopChartsCategory(type = TopChartsType(podcast = false, podcastEpisode = true))
            }
        }
    }
}