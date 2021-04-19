package com.caldeirasoft.outcast

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.facebook.stetho.Stetho
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import timber.log.Timber

@FlowPreview
@ExperimentalCoroutinesApi
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Mavericks.initialize(this@App)

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