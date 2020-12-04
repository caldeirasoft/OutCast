package com.caldeirasoft.outcast.ui.screen.store

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.HourglassFull
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.caldeirasoft.outcast.domain.interfaces.StoreItem
import com.caldeirasoft.outcast.domain.models.*
import com.caldeirasoft.outcast.ui.ambient.ActionsAmbient
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.navigation.Actions
import com.caldeirasoft.outcast.ui.screen.store.discover.Discover
import com.caldeirasoft.outcast.ui.screen.store.topcharts.TopCharts
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.datetime.Clock
import org.koin.androidx.compose.getViewModel

typealias NavigateToStoreEntryCallBack = (String) -> Unit



@FlowPreview
@ExperimentalCoroutinesApi
@Composable
fun StoreScreen(viewModel: StoreViewModel = getViewModel()) {
    val actions = ActionsAmbient.current
    val viewState by viewModel.state.collectAsState()

    println("Compose StoreDirectoryScreen : ${Clock.System.now()}")

    StoreTabLayout(
        tabs = viewState.storeTabs,
        selectedTab = viewState.selectedStoreTab,
        onTabSelected = viewModel::onStoreTabSelected,
        actions = actions
    )
}

@Composable
fun StoreTabLayout(
    tabs: List<StoreTab>,
    selectedTab: StoreTab,
    onTabSelected: (StoreTab) -> Unit,
    actions: Actions
) {
    val selectedTabIndex = tabs.indexOfFirst { it == selectedTab }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Discover")
                },
                navigationIcon = {
                    IconButton(onClick = actions.navigateUp) {
                        Icon(Icons.Filled.ArrowBack)
                    }
                },
                actions = {
                    IconButton(onClick = actions.navigateUp) {
                        Icon(asset = Icons.Filled.HourglassFull)
                    }
                },
                backgroundColor = Color.Transparent
            )
        }
    )
    {
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
                                    StoreTab.DISCOVER -> "Discover"
                                    StoreTab.CATEGORIES -> "Categories"
                                    StoreTab.CHARTS -> "Charts"
                                },
                                style = MaterialTheme.typography.body2
                            )
                        }
                    )
                }
            }
            Surface(modifier = Modifier.weight(0.5f)) {
                when (selectedTabIndex) {
                    StoreTab.DISCOVER.ordinal -> Discover()
                    StoreTab.CHARTS.ordinal -> TopCharts()
                }
            }
        }
    }
}