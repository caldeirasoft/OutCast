package com.caldeirasoft.outcast

import android.app.Application
import com.caldeirasoft.outcast.di.appModule
import com.caldeirasoft.outcast.di.initKoinModules
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            initKoinModules(appModule = appModule)
        }
    }

}