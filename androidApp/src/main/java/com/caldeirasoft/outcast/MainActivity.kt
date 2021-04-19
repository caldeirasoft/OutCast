package com.caldeirasoft.outcast

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.caldeirasoft.outcast.ui.navigation.MainNavHost
import com.caldeirasoft.outcast.ui.navigation.ScreenName
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.systemuicontroller.LocalSystemUiController
import com.google.accompanist.systemuicontroller.rememberAndroidSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalMaterialApi
@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("Locale", applicationContext.resources.configuration.locales.get(0).toString())
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContent {
            val controller = rememberAndroidSystemUiController()
            CompositionLocalProvider(LocalSystemUiController provides controller) {
                OutCastTheme {
                    // Get the current SystemUiController
                    val systemUiController = LocalSystemUiController.current
                    val useDarkIcons = MaterialTheme.colors.isLight
                    SideEffect {
                        // Update all of the system bar colors to be transparent, and use
                        // dark icons if we're in light theme
                        systemUiController.setStatusBarColor(
                            color = Color.Transparent,
                            darkIcons = useDarkIcons
                        )
                    }

                    ProvideWindowInsets {
                        MainNavHost(ScreenName.DISCOVER)
                    }
                }
            }
        }
    }
}
