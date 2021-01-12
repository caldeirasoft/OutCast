object Versions {
    const val jvm = "1.8"
    const val kotlin = "1.4.20"
    const val coroutines = "1.3.9"
    const val serialization = "1.0.0"
    const val appcompat = "1.2.0"
    const val support = "1.3.1"
    const val room = "2.2.5"

    const val ktor = "1.4.0"
    const val sqldelight = "1.4.3"
    const val datetime = "0.1.0"

    const val junit = "4.13"
}

object AndroidSdk {
    const val min = 24
    const val compile = 30
    const val target = compile
}

object Dependencies {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha03"
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
            private const val version = "1.0.0-alpha04"
            const val compose = "androidx.navigation:navigation-compose:$version"
        }

        object Compose {
            const val version = "1.0.0-alpha09"

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
        const val stdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$version"
        const val gradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$version"
    }

    object Coroutines {
        private const val version = "1.4.0"
        const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
        const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:$version"
        const val test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:$version"
    }

    object Kotlinx {
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0"
        const val datetime = "org.jetbrains.kotlinx:kotlinx-datetime:0.1.0"
    }

    object Accompanist {
        private const val version = "0.3.3.1"
        const val coil = "dev.chrisbanes.accompanist:accompanist-coil:$version"
    }

    object Landscapist {
        private const val version = "1.1.2"
        const val coil = "com.github.skydoves:landscapist-coil:$version"
    }

    object Koin {
        private const val version = "2.2.1"
        const val core = "org.koin:koin-core:$version"
        const val coreExt = "org.koin:koin-core-ext:$version"
        const val test = "org.koin:koin-test:$version"
        const val Ktor = "org.koin:koin-ktor:$version"
        const val android = "org.koin:koin-android:$version"
        const val androidScope = "org.koin:koin-androidx-scope:$version"
        const val androidViewModel = "org.koin:koin-androidx-viewmodel:$version"
        const val androidCompose = "org.koin:koin-androidx-compose:$version"
        const val androidExt = "org.koin:koin-androidx-ext:$version"
        const val gradlePlugin = "org.koin:koin-gradle-plugin:$version"
    }

    object Ktor {
        private const val version = "1.5.0"
        const val clientCore = "io.ktor:ktor-client-core:$version"
        const val clientAndroid = "io.ktor:ktor-client-android:$version"
        const val clientOkHttp = "io.ktor:ktor-client-okhttp:$version"
        const val clientJvm = "io.ktor:ktor-client-core-jvm:$version"
        const val clientJs = "io.ktor:ktor-client-core-js:$version"
        const val clientMock = "io.ktor:ktor-client-mock:$version"
        const val clientLogging = "io.ktor:ktor-client-logging:$version"

        const val serialization = "io.ktor:ktor-client-serialization:$version"
        const val serializationJvm = "io.ktor:ktor-client-serialization-jvm:$version"

        const val features = "io.ktor:ktor-features:$version"
        const val encoding = "io.ktor:ktor-client-encoding:$version"
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
        }

        object LeakCanary {
            private const val version = "1.6.1"
            const val leakCanaryRelease = "com.squareup.leakcanary:leakcanary-android-no-op:$version"
            const val leakCanaryDebug = "com.squareup.leakcanary:leakcanary-android:$version"
        }
    }

    object Facebook {
        object Flipper {
            private const val version = "0.67.0"
            const val flipperDebug = "com.facebook.flipper:flipper:$version"
            const val flipperRelease = "com.facebook.flipper:flipper-noop:$version"
            const val network = "com.facebook.flipper:flipper-network-plugin:$version"
            const val leakCanary = "com.facebook.flipper:flipper-leakcanary-plugin:$version"
        }

        object SoLoader {
            private const val version = "0.9.0"
            const val soloader = "com.facebook.soloader:soloader:$version"
        }
    }


    object Plist {
        private const val version = "1.23"
        const val ddPlist = "com.googlecode.plist:dd-plist:$version"
    }
}