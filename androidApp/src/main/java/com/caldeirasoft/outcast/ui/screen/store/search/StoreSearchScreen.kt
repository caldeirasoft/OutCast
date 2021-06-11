package com.caldeirasoft.outcast.ui.screen.store.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.components.*
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.AppbarContainer
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.CollapsingToolbar
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.ScrollStrategy
import com.caldeirasoft.outcast.ui.components.collapsingtoolbar.rememberCollapsingToolbarState
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.screen.store.categories.drawableId
import com.caldeirasoft.outcast.ui.theme.typography
import com.caldeirasoft.outcast.ui.util.toDp
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StoreSearchScreen(
    navigateTo: (Screen) -> Unit,
) {
    val listState = rememberLazyListState(0)

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        BoxWithConstraints {
            val screenHeight = constraints.maxHeight
            val headerRatio: Float = 1 / 3f
            val headerHeight = remember { mutableStateOf((screenHeight * headerRatio).toInt()) }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 56.dp)
            )
            {
                // header
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(height = headerHeight.value.toDp())
                    ) {
                        Text(
                            text = stringResource(id = R.string.screen_search),
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(top = 16.dp, bottom = 16.dp)
                                .padding(start = 16.dp, end = 16.dp),
                            style = typography.h4
                        )
                    }
                }

                // search bar
                stickyHeader {
                    SearchBar(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }

                val categoriesIds = listOf(-1) + Category.values()
                    .filter { !it.nested }
                    .map { it.id }

                item {
                    // header
                    StoreHeadingSection(title = stringResource(id = R.string.store_tab_categories))
                }
                gridItems(
                    items = categoriesIds,
                    contentPadding = PaddingValues(16.dp),
                    horizontalInnerPadding = 8.dp,
                    verticalInnerPadding = 8.dp,
                    columns = 2
                ) { categoryId ->
                    when (categoryId) {
                        -1 -> TopChartCardItem(
                            navigateToTopChart = { navigateTo(Screen.StoreDataScreen(StoreData.TopCharts)) }
                        )
                        else -> {
                            Category.fromId(categoryId)?.let { category ->
                                CategoryCardItem(
                                    category = category,
                                    navigateToCategory = {
                                        navigateTo(
                                            Screen.StoreDataScreen(
                                                category
                                            )
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(modifier: Modifier = Modifier)
{
    // search button
    OutlinedButton(
        onClick = {},
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        colors = ButtonDefaults.textButtonColors(
            backgroundColor = MaterialTheme.colors.surface,
            contentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.medium),
            disabledContentColor = MaterialTheme.colors.onSurface
                .copy(alpha = ContentAlpha.disabled)
        ),
        shape = RoundedCornerShape(50)
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(imageVector = Icons.Filled.Search,
                contentDescription = null,
            )
            Text("Search", modifier = Modifier.padding(horizontal = 4.dp))
        }
    }
}

@Composable
fun TopChartCardItem(
    navigateToTopChart: (StoreItemType) -> Unit,
) {
    Card(
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 9f)
            .clickable(onClick = {
                navigateToTopChart(StoreItemType.PODCAST)
            })
    )
    {
        ListItem(
            modifier = Modifier,
            text = { Text(text = stringResource(id = R.string.store_tab_chart_podcasts)) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.TrendingUp,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        )
    }
}

@Composable
fun CategoryCardItem(
    category: Category,
    navigateToCategory: (Category) -> Unit,
) {
    Card(
        border = ButtonDefaults.outlinedBorder,
        shape = RoundedCornerShape(16.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 9f)
            .clickable(onClick = {
                navigateToCategory(category)
            })
    )
    {
        ListItem(
            modifier = Modifier,
            text = { Text(text = category.text) },
            icon = {
                Image(
                    painter = painterResource(id = category.drawableId),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        )
    }
}
