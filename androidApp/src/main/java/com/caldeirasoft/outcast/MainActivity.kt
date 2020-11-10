package com.caldeirasoft.outcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.ui.platform.setContent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caldeirasoft.outcast.presentation.viewmodel.InboxViewModel
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.screen.store.StoreDiscoverScreen
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    val model : InboxViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OutCastTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "inbox",
                ) {
                    composable("inbox") { navBackStackEntry ->
                        InboxScreen(
                            model2 = model,
                            navController = navController,
                            scope = lifecycleScope
                        )
                    }
                    composable("discover") { navBackStackEntry ->
                        StoreDiscoverScreen(navController = navController)
                    }
                }
            }
        }
    }
}
