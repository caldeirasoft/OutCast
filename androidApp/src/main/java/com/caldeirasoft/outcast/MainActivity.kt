package com.caldeirasoft.outcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.ui.platform.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.caldeirasoft.outcast.ui.screen.inbox.InboxScreen
import com.caldeirasoft.outcast.ui.theme.OutCastTheme

class MainActivity : AppCompatActivity() {
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
                        InboxScreen(navController = navController)
                    }
                }
            }
        }
    }
}
