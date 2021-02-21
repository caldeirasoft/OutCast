package com.caldeirasoft.outcast

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.platform.setContent
import com.caldeirasoft.outcast.ui.navigation.MainNavHost
import com.caldeirasoft.outcast.ui.navigation.Screen
import com.caldeirasoft.outcast.ui.navigation.ScreenName
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@ExperimentalMaterialApi
@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("Locale", applicationContext.resources.configuration.locales.get(0).toString())
        //WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContent {
            OutCastTheme {
                MainNavHost(ScreenName.STORE_DISCOVER)
            }
        }
    }
}
