package com.caldeirasoft.outcast.ui.screen.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.domain.enums.StoreItemType
import com.caldeirasoft.outcast.domain.models.Category
import com.caldeirasoft.outcast.domain.models.store.StoreData
import com.caldeirasoft.outcast.ui.components.ScaffoldWithLargeHeaderAndLazyColumn
import com.caldeirasoft.outcast.ui.components.StoreHeadingSection
import com.caldeirasoft.outcast.ui.components.gridItems
import com.caldeirasoft.outcast.ui.screen.store.categories.drawableId
import com.caldeirasoft.outcast.ui.screen.store.storedata.RoutesActions
import com.caldeirasoft.outcast.ui.util.navigateToStore
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding
import cz.levinzonr.router.core.Route

@OptIn(ExperimentalFoundationApi::class)
@Route(name = "search")
@Composable
fun SearchScreen(
    navController: NavController
) {
    ScaffoldWithLargeHeaderAndLazyColumn(
        title = stringResource(id = R.string.screen_search),
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        showTopBar = false,
    ) {
        // search bar
        stickyHeader {
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = { navController.navigate(RoutesActions.toSearch_results()) }
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
                    navigateToTopChart = { navController.navigateToStore(StoreData.TopCharts) }
                )
                else -> {
                    Category.fromId(categoryId)?.let { category ->
                        CategoryCardItem(
                            category = category,
                            navigateToCategory = {
                                navController.navigateToStore(category)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
)
{
    // search button
    OutlinedButton(
        onClick = onClick,
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
        onClick = {
            navigateToTopChart(StoreItemType.PODCAST)
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 9f)
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
        onClick = {
            navigateToCategory(category)
        },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(24 / 9f)
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
