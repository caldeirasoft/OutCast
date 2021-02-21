object Versions {
    const val jvm = "1.8"
    const val coroutines = "1.3.9"
    const val serialization = "1.0.0"
    const val appcompat = "1.2.0"
    const val support = "1.3.1"
    const val room = "2.2.5"

    const val junit = "4.13"
}

object Kotlin {
    const val version = "1.4.30"
}


object Libs {
    object GradlePlugin {
        const val android = "com.android.tools.build:gradle:7.0.0-alpha06"
    }

    object Kotlinx {
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:_"
    }

    object AndroidX {
        object Compose {
            const val layout = "androidx.compose.foundation:foundation-layout:_"
            const val tooling = "androidx.compose.ui:ui-tooling:_"
            const val iconsExtended = "androidx.compose.material:material-icons-extended:_"
        }

        object Activity {
            const val compose = "androidx.activity:activity-compose:_"
        }

        object Lifecycle {
            private const val composeVersion = "1.0.0-alpha01"
            const val viewModelCompose = "androidx.lifecycle:lifecycle-viewmodel-compose:$composeVersion"
        }

        object Navigation {
            const val compose = "androidx.navigation:navigation-compose:_"
        }

        object Paging {
            const val compose = "androidx.paging:paging-compose:_"
        }

        object DataStore {
            const val datastore = "androidx.datastore:datastore:_"
            const val preferences = "androidx.datastore:datastore-preferences:_"
        }
    }

    object Koin {
        const val core = "org.koin:koin-core:_"
        const val coreExt = "org.koin:koin-core-ext:_"
        const val test = "org.koin:koin-test:_"
        const val Ktor = "org.koin:koin-ktor:_"
        const val androidx = "org.koin:koin-androidx-scope:_"
        const val androidExt = "org.koin:koin-androidx-ext:_"
        const val androidWorkManager = "org.koin:koin-androidx-workmanager:_"
        const val androidCompose = "org.koin:koin-androidx-compose:_"
        const val gradlePlugin = "org.koin:koin-gradle-plugin:_"
    }

    object OkLog3 {
        const val core = "com.github.simonpercic:oklog3:_"
    }

    object okHttp3 {
        const val dns = "com.squareup.okhttp3:okhttp-dnsoverhttps:_"
    }

    object Chucker {
        const val library = "com.github.chuckerteam.chucker:library:_"
        const val libraryNoOp = "com.github.chuckerteam.chucker:library-no-op:_"
    }

    object Stetho {
        const val runtime = "com.facebook.stetho:stetho:_"
        const val okhttp3 = "com.facebook.stetho:stetho-okhttp3:_"
    }

    object SqlDelight {
        const val coroutines = "com.squareup.sqldelight:coroutines-extensions-jvm:_"
    }

    object Landscapist {
        const val coil = "com.github.skydoves:landscapist-coil:_"
    }
}


/*
object Android {
    const val gradlePlugin = "com.android.tools.build:gradle:_"
    const val junit = "junit:junit:4.13"
}

object AndroidX {
    const val appcompat = "androidx.appcompat:appcompat:_"
    const val palette = "androidx.palette:palette:_"
    const val coreKtx = "androidx.core:core-ktx:_"

    const val runtime = "androidx.lifecycle:lifecycle-runtime-ktx:_"
    const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx_"

    const val datastore = "androidx.datastore:datastore:_"
    const val preferences = "androidx.datastore:datastore-preferences:_"
}


object Kotlinx {
    object Coroutines {
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:_"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:_"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:_"
    }

    const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:_"
}



object Ktor {
    private const val version = "1.5.0"
    const val clientCore = "io.ktor:ktor-client-core:_"
    const val clientAndroid = "io.ktor:ktor-client-android:_"
    const val clientOkHttp = "io.ktor:ktor-client-okhttp:_"
    const val clientJvm = "io.ktor:ktor-client-core-jvm:_"
    const val clientJs = "io.ktor:ktor-client-core-js:_"
    const val clientMock = "io.ktor:ktor-client-mock:_"
    const val clientLogging = "io.ktor:ktor-client-logging:_"

    const val serialization = "io.ktor:ktor-client-serialization:_"
    const val serializationJvm = "io.ktor:ktor-client-serialization-jvm:_"

    const val features = "io.ktor:ktor-features:_"
    const val encoding = "io.ktor:ktor-client-encoding:_"
}


object SqlDelight {
    const val gradlePlugin = "com.squareup.sqldelight:gradle-plugin:_"
    const val runtime = "com.squareup.sqldelight:runtime:_"
    const val runtimeJvm = "com.squareup.sqldelight:runtime-jvm:_"
    const val runtimeJs = "com.squareup.sqldelight:runtime-js:_"
    const val sqlDriver = "com.squareup.sqldelight:sqlite-driver:_"
    const val androidDriver = "com.squareup.sqldelight:android-driver:_"
    const val coroutines = "com.squareup.sqldelight:coroutines-extensions:_"
}

object OkHttp3 {
    const val okhttp = "com.squareup.okhttp3:okhttp:_"
    const val dns = "com.squareup.okhttp3:okhttp-dnsoverhttps:_"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:_"
}

object Retrofit {
    const val retrofit = "com.squareup.retrofit2:retrofit:_"

    object KotlinXSerialization {
        const val serialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:_"
    }
}

object LeakCanary {
    const val leakCanaryRelease = "com.squareup.leakcanary:leakcanary-android-no-op:_"
    const val leakCanaryDebug = "com.squareup.leakcanary:leakcanary-android:_"
}



object Plist {
    const val ddPlist = "com.googlecode.plist:dd-plist:_"
}

 */