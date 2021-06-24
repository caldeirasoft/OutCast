package com.caldeirasoft.outcast.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Subscriptions
import androidx.compose.material.icons.twotone.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.caldeirasoft.outcast.R
import com.caldeirasoft.outcast.ui.screen.store.storedata.Routes

sealed class RootScreen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon,
) {
    object Inbox : RootScreen(
        Routes.inbox.path,
        R.string.screen_inbox,
        Icons.Outlined.Inbox,
        Icons.Filled.Inbox)

    object Library : RootScreen(
        Routes.library.path,
        R.string.screen_library,
        Icons.Outlined.Subscriptions,
        Icons.Filled.Subscriptions)

    object Discover : RootScreen(
        Routes.discover.path,
        R.string.screen_discover,
        Icons.Outlined.Explore,
        Icons.Filled.Explore
    )

    object Search : RootScreen(
        Routes.search.path,
        R.string.screen_search,
        Icons.Outlined.Search,
        Icons.TwoTone.Search)
}

val bottomNavItems = listOf(
    RootScreen.Inbox,
    RootScreen.Library,
    RootScreen.Discover,
    RootScreen.Search,
)

@Composable
fun SetupBottomNavBar(
    navController: NavController,
) {
    BottomNavigation(backgroundColor = MaterialTheme.colors.surface) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val backQueue = navController.backQueue
        val rootDestinationsQueue = navController
            .backQueue
            .map { it.destination.route }
            .filter { route -> bottomNavItems.any { it.route == route } }

        bottomNavItems.forEach { screen ->
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = if (rootDestinationsQueue.lastOrNull() == screen.route)
                            screen.selectedIcon
                        else screen.icon,
                        contentDescription = stringResource(id = screen.resourceId),
                    )
                },
                selected = (rootDestinationsQueue.lastOrNull() == screen.route),
                selectedContentColor = MaterialTheme.colors.primary,
                unselectedContentColor = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                onClick = {
                    navController.navigate(screen.route) {
                        // Avoid multiple copies of the same destination when re-selecting the same item
                        launchSingleTop = true
                        restoreState = true
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                    }
                },
            )
        }
    }
}