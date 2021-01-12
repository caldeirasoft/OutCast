package com.caldeirasoft.outcast

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.caldeirasoft.outcast.ui.navigation.*
import com.caldeirasoft.outcast.ui.theme.OutCastTheme
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview

@FlowPreview
@ExperimentalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d("Locale", applicationContext.resources.configuration.locales.get(0).toString())

        super.onCreate(savedInstanceState)
        setContent {
            OutCastTheme {
                MainNavHost(Screen.STORE_DIRECTORY)
            }
        }
    }
}
