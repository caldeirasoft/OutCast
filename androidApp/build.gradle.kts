plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Dependencies.Kotlin.version
}

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
    maven(url = uri("https://dl.bintray.com/ekito/koin/")) // koin
    maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
    maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
}
dependencies {
    implementation(project(":shared"))
    // Android
    implementation("com.google.android.material:material:1.3.0")
    // AndroidX
    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.palette)
    // LifeCycle
    implementation(Dependencies.AndroidX.LifeCycle.runtime)
    implementation(Dependencies.AndroidX.LifeCycle.ViewModel.ktx)
    //implementation(Dependencies.AndroidX.LifeCycle.ViewModel.compose)
    // Activity Compose
    implementation(Dependencies.AndroidX.Activity.compose)
    // Compose
    implementation(Dependencies.AndroidX.Compose.runtime)
    implementation(Dependencies.AndroidX.Compose.foundation)
    implementation(Dependencies.AndroidX.Compose.ui)
    implementation(Dependencies.AndroidX.Compose.layout)
    implementation(Dependencies.AndroidX.Compose.animation)
    implementation(Dependencies.AndroidX.Compose.material)
    implementation(Dependencies.AndroidX.Compose.iconsExtended)
    implementation(Dependencies.AndroidX.Compose.tooling)
    // Navigation
    implementation(Dependencies.AndroidX.Navigation.compose)
    // Paging
    implementation(Dependencies.AndroidX.Paging.compose)
    // Kotlin coroutines
    implementation(Dependencies.Kotlinx.Coroutines.core)
    // Kotlinx serialization
    implementation(Dependencies.Kotlinx.Serialization.serialization)
    // Kotlinx datetime
    implementation(Dependencies.Kotlinx.DateTime.datetime)
    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.androidExt)
    //implementation(Dependencies.Koin.androidCompose)
    implementation(Dependencies.Koin.androidWorkManager)
    //    implementation(Libs.Koin.androidExt)
    // Timber
    implementation(Dependencies.Timber.timber)
    // OkLog3
    implementation(Dependencies.OkLog3.okLog3)
    // Accompanist
    //implementation(Dependencies.Accompanist.insets)
    // Landscapist
    implementation(Dependencies.Landscapist.coil)
    // Retrofit
    implementation(Dependencies.SquareUp.Retrofit.retrofit)
    implementation(Dependencies.SquareUp.Retrofit.KotlinXSerialization.serialization)
    // Plist
    implementation(Dependencies.Plist.ddPlist)
    // Stetho
    implementation(Dependencies.Stetho.runtime)
    implementation(Dependencies.Stetho.okhttp3)
    // Leak Canary
    debugImplementation(Dependencies.SquareUp.LeakCanary.leakCanaryDebug)
    releaseImplementation(Dependencies.SquareUp.LeakCanary.leakCanaryRelease)
    // Java 8+ API desugaring support
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:1.1.1")
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
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerVersion = Dependencies.Kotlin.version
        kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.version
    }
    buildToolsVersion = "30.0.2"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs += listOf(
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=androidx.compose.foundation.lazy.ExperimentalLazyDsl",
            "-Xskip-prerelease-check",
            "-Xallow-unstable-dependencies",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}
