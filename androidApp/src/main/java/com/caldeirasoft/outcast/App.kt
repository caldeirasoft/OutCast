package com.caldeirasoft.outcast

import android.app.Application
import com.caldeirasoft.outcast.di.appModule
import com.caldeirasoft.outcast.di.initKoinModules
import com.facebook.stetho.Stetho
import com.squareup.leakcanary.LeakCanary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            initKoinModules(appModule = appModule)
        }

        if (BuildConfig.DEBUG) {
            initErrorHandler()
            initStetho()
            initLeakDetection()
        }
        setupTheme()
    }

    private fun initErrorHandler() {
        val exceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { t, e ->
            exceptionHandler?.uncaughtException(t, e)
        }
    }

    private fun initStetho() {
        Stetho.initializeWithDefaults(this)
    }

    private fun initLeakDetection() {
        if (BuildConfig.DEBUG) {
            LeakCanary.install(this)
        }
    }

    private fun setupTheme() {
        //ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
    }

}