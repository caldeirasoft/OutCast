plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") apply true
    kotlin("plugin.parcelize") apply true
}

// get compose version in gradle.properties file
val composeVersion: String by project

android {

    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.caldeirasoft.outcast.androidApp"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "0.1"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
        useIR = true
        freeCompilerArgs = freeCompilerArgs + listOf(
            "-Xopt-in=androidx.compose.foundation.lazy.ExperimentalLazyDsl",
            "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-Xopt-in=androidx.compose.runtime.ExperimentalComposeApi",
            "-Xopt-in=dev.chrisbanes.accompanist.insets.ExperimentalAnimatedInsets",
            "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
            "-Xopt-in=kotlin.time.ExperimentalTime",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }
}

dependencies {
    implementation(project(":shared"))
    // Android
    implementation("com.google.android.material:material:1.3.0")
    // AndroidX
    api(libs.bundles.kotlin)
    api(libs.bundles.stetho)
    api(libs.bundles.retrofit)
    api(libs.bundles.okhttp)
    api(libs.bundles.koin)
    api(libs.bundles.compose)
    api(libs.bundles.sqldelight)
    api(libs.bundles.accompanist)
    api(libs.core.ktx)
    api(libs.appcompat)
    api(libs.palette)
    api(libs.runtime.ktx)
    api(libs.datastore.preferences)
    api(libs.appcompat)
    api(libs.activity.compose)
    api(libs.viewmodel.compose)
    api(libs.navigation.compose)
    api(libs.paging.compose)
    api(libs.landscapist.coil)
    api(libs.timber)
    api(libs.mavericks)
    releaseImplementation(libs.chucker.release)
    debugImplementation(libs.chucker.debug)
    // Java 8+ API desugaring support
    coreLibraryDesugaring(libs.desugarJdkLibs)
}
