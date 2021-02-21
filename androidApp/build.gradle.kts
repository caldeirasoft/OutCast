plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Kotlin.version
    id("com.squareup.sqldelight")
}

android {
    compileSdkVersion(Constants.compileSdk)
    defaultConfig {
        applicationId = "com.caldeirasoft.outcast"
        minSdkVersion(24)
        targetSdkVersion(Constants.targetSdk)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig =
                if (System.getenv("CI") == "true") signingConfigs.getByName("release")
                else getByName("debug").signingConfig
        }

        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-DEBUG"
            isDebuggable = true
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=androidx.compose.foundation.lazy.ExperimentalLazyDsl",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.runtime.ExperimentalComposeApi",
            "-Xopt-in=dev.chrisbanes.accompanist.insets.ExperimentalAnimatedInsets",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlin.time.ExperimentalTime"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        val versionsProperties = rootProject.propertiesFromFile("versions.properties")
        kotlinCompilerExtensionVersion = versionsProperties.getProperty("version.androidx.compose.foundation")
    }
}

sqldelight {
    database("Database") {
        packageName = "com.caldeirasoft.outcast"
        schemaOutputDirectory = file("build/dbs")
        dialect = "sqlite:3.24"
    }
}

dependencies {
    implementation(project(":shared"))
    // Android
    implementation("com.google.android.material:material:_")
    // AndroidX
    implementation(AndroidX.core.ktx)
    implementation(AndroidX.appCompat)
    implementation(AndroidX.paletteKtx)
    implementation(AndroidX.Lifecycle.runtime)
    implementation(Libs.AndroidX.Activity.compose)
    implementation(Libs.AndroidX.Lifecycle.viewModelCompose)
    implementation(Libs.AndroidX.DataStore.preferences)
    // Compose
    implementation(AndroidX.Compose.runtime)
    implementation(AndroidX.Compose.ui)
    implementation(AndroidX.Compose.animation)
    implementation(AndroidX.Compose.foundation)
    implementation(AndroidX.Compose.material)
    implementation(Libs.AndroidX.Compose.layout)
    implementation(Libs.AndroidX.Compose.iconsExtended)
    implementation(Libs.AndroidX.Compose.tooling)
    implementation(Libs.AndroidX.Navigation.compose)
    implementation(Libs.AndroidX.Paging.compose)
    // Kotlin
    implementation(KotlinX.coroutines.core)
    implementation(KotlinX.coroutines.android)
    implementation(KotlinX.serialization.json)
    implementation(Libs.Kotlinx.datetime)
    // Koin
    implementation(Libs.Koin.androidx)
    implementation(Libs.Koin.androidExt)
    implementation(Libs.Koin.androidWorkManager)
    // Libs
    implementation(Libs.OkLog3.core)
    implementation(Square.retrofit2.retrofit)
    implementation(JakeWharton.retrofit2.converter.kotlinxSerialization)
    implementation(JakeWharton.timber)
    implementation(Square.okHttp3.okHttp)
    implementation(Square.okHttp3.loggingInterceptor)
    implementation(Libs.okHttp3.dns)
    implementation(Square.sqlDelight.drivers.android)
    implementation(Libs.SqlDelight.coroutines)
    implementation(Libs.Stetho.runtime)
    implementation(Libs.Stetho.okhttp3)
    debugImplementation(Libs.Chucker.library)
    releaseImplementation(Libs.Chucker.libraryNoOp)
    debugImplementation(Square.LeakCanary.android)
    implementation(Libs.Landscapist.coil)
    // Java 8+ API desugaring support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:_")
}
