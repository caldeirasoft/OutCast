object Versions {
    const val jvm = "1.8"
    const val kotlin = "1.4.30"
    const val coroutines = "1.3.9"
    const val serialization = "1.0.0"
    const val appcompat = "1.2.0"
    const val support = "1.3.1"
    const val room = "2.2.5"

    const val junit = "4.13"
}

object AndroidSdk {
    const val min = 24
    const val compile = 30
    const val target = compile
}

object Dependencies {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha05"
    const val junit = "junit:junit:4.13"

    object AndroidX {
        const val appcompat = "androidx.appcompat:appcompat:1.2.0"
        const val palette = "androidx.palette:palette:1.0.0"
        const val coreKtx = "androidx.core:core-ktx:1.5.0-alpha02"

        object LifeCycle {
            private const val lifecycleVersion = "2.3.0-beta01"
            const val lifecycle = "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion"
            const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion"
        }

        object Navigation {
            private const val version = "1.0.0-alpha05"
            const val compose = "androidx.navigation:navigation-compose:$version"
        }

        object Compose {
            const val version = "1.0.0-alpha10"

            const val ui = "androidx.compose.ui:ui:$version"
            const val runtime = "androidx.compose.runtime:runtime:$version"
            const val foundation = "androidx.compose.foundation:foundation:$version"
            const val layout = "androidx.compose.foundation:foundation-layout:$version"
            const val material = "androidx.compose.material:material:$version"
            const val animation = "androidx.compose.animation:animation:$version"
            const val tooling = "androidx.compose.ui:ui-tooling:$version"
            const val iconsExtended = "androidx.compose.material:material-icons-extended:$version"
        }

        object Paging {
            private const val version = "1.0.0-alpha04"
            const val compose = "androidx.paging:paging-compose:$version"
        }

        object DataStore {
            private const val version = "1.0.0-alpha04"
            const val datastore = "androidx.datastore:datastore:$version"
            const val preferences = "androidx.datastore:datastore-preferences:$version"
        }
    }

    object Kotlin {
        const val version = "1.4.21"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Coroutines {
        private const val version = "1.4.2"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Kotlinx {
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.0"
    }

    object Accompanist {
        private const val version = "0.4.2"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
        const val insets = "dev.chrisbanes.accompanist:accompanist-insets:$version"
    }

    object Landscapist {
        private const val version = "1.1.2"
        const val coil = "com.github.skydoves:landscapist-coil:$version"
    }

    object Chucker {
        private const val version = "3.4.0"
        const val library = "com.github.chuckerteam.chucker:library:$version"
        const val libraryNoOp = "com.github.chuckerteam.chucker:library-no-op:$version"
    }

    object Koin {
        private const val version = "2.2.1"
        const val core = "org.koin:koin-core:$version"
        const val coreExt = "org.koin:koin-core-ext:$version"
        const val test = "org.koin:koin-test:$version"
        const val android = "org.koin:koin-android:$version"
        const val androidScope = "org.koin:koin-androidx-scope:$version"
        const val androidViewModel = "org.koin:koin-androidx-viewmodel:$version"
        const val androidCompose = "org.koin:koin-androidx-compose:$version"
        const val androidExt = "org.koin:koin-androidx-ext:$version"
        const val gradlePlugin = "org.koin:koin-gradle-plugin:$version"
    }

    object OkLog3 {
        private const val version = "2.3.0"
        const val okLog3 = "com.github.simonpercic:oklog3:$version"
    }

    object Timber {
        private const val version = "4.7.1"
        const val timber = "com.jakewharton.timber:timber:$version"
    }

    object SquareUp {
        object SqlDelight {
            private const val version = "1.4.3"
            const val gradlePlugin = "com.squareup.sqldelight:gradle-plugin:$version"
            const val runtime = "com.squareup.sqldelight:runtime:$version"
            const val runtimeJvm = "com.squareup.sqldelight:runtime-jvm:$version"
            const val runtimeJs = "com.squareup.sqldelight:runtime-js:$version"
            const val sqlDriver = "com.squareup.sqldelight:sqlite-driver:$version"
            const val androidDriver = "com.squareup.sqldelight:android-driver:$version"
            const val coroutines = "com.squareup.sqldelight:coroutines-extensions:$version"
        }

        object OkHttp3 {
            private const val version = "4.9.0"
            const val okhttp = "com.squareup.okhttp3:okhttp:$version"
            const val dns = "com.squareup.okhttp3:okhttp-dnsoverhttps:$version"
            const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:$version"
        }

        object Retrofit {
            private const val version = "2.9.0"
            const val retrofit = "com.squareup.retrofit2:retrofit:$version"

            object KotlinXSerialization {
                private const val version = "0.8.0"
                const val serialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:$version"
            }
        }

        object LeakCanary {
            private const val version = "1.6.1"
            const val leakCanaryRelease = "com.squareup.leakcanary:leakcanary-android-no-op:$version"
            const val leakCanaryDebug = "com.squareup.leakcanary:leakcanary-android:$version"
        }
    }

    object Stetho {
        private const val version = "1.5.1"
        const val runtime = "com.facebook.stetho:stetho:$version"
        const val okhttp3 = "com.facebook.stetho:stetho-okhttp3:$version"
    }

    object Plist {
        private const val version = "1.23"
        const val ddPlist = "com.googlecode.plist:dd-plist:$version"
    }
}