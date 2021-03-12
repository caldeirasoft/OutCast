package com.caldeirasoft.outcast

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.caldeirasoft.outcast.di.initKoinModules
import com.facebook.stetho.Stetho
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Mavericks.initialize(this@App)
        startKoin {
            androidLogger()
            androidContext(this@App)
            initKoinModules()
        }

        if (BuildConfig.DEBUG) {
            initErrorHandler()
            initStetho()
            initLeakDetection()
            Timber.plant(Timber.DebugTree());
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
            //LeakCanary.install(this)
        }
    }

    private fun setupTheme() {
        //ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
    }

}