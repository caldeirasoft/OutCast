package com.caldeirasoft.outcast

import android.app.Application
import com.caldeirasoft.outcast.di.appModule
import com.caldeirasoft.outcast.di.initKoinModules
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.leakcanary.LeakCanaryFlipperPlugin
import com.facebook.flipper.plugins.leakcanary.RecordLeakService
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sandbox.SandboxFlipperPlugin
import com.facebook.flipper.plugins.sandbox.SandboxFlipperPluginStrategy
import com.facebook.soloader.SoLoader
import com.squareup.leakcanary.LeakCanary
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

@FlowPreview
@ExperimentalCoroutinesApi
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SoLoader.init(this, false)

        startKoin {
            androidContext(this@App)
            initKoinModules(appModule = appModule)
        }

        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            AndroidFlipperClient.getInstance(this).also { client ->
                client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
                client.addPlugin(DatabasesFlipperPlugin(this))
                client.addPlugin(LeakCanaryFlipperPlugin())
                val networkFlipperPlugin = get<NetworkFlipperPlugin>()
                client.addPlugin(networkFlipperPlugin)

                LeakCanary.refWatcher(this)
                    .listenerServiceClass(RecordLeakService::class.java)
                    .buildAndInstall()

                client.start()
            }
        }
    }

}