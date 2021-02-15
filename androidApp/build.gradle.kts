plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Dependencies.Kotlin.version
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

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
    val appCompatVersion: String by project
    val coil = "1.1.4"
    val kotlinxDatetimeVersion: String by project

    implementation(project(":shared"))
    // Android
    implementation("com.google.android.material:material:1.3.0")
    // AndroidX
    implementation("androidx.core:core-ktx:1.5.0-beta01")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.palette:palette:1.0.0")
    // LifeCycle
    //implementation(Dependencies.AndroidX.LifeCycle.runtime)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.0")
    //implementation(Dependencies.AndroidX.LifeCycle.ViewModel.compose)
    // Activity Compose
    //implementation(Dependencies.AndroidX.Activity.compose)
    // Compose
    implementation(Dependencies.AndroidX.Compose.runtime)
    //implementation(Dependencies.AndroidX.Compose.foundation)
    implementation(Dependencies.AndroidX.Compose.ui)
    //implementation(Dependencies.AndroidX.Compose.layout)
    implementation(Dependencies.AndroidX.Compose.animation)
    implementation(Dependencies.AndroidX.Compose.material)
    implementation(Dependencies.AndroidX.Compose.iconsExtended)
    implementation(Dependencies.AndroidX.Compose.tooling)
    implementation(Dependencies.AndroidX.Navigation.compose)
    implementation(Dependencies.AndroidX.Paging.compose)
    // Kotlin coroutines
    implementation(Dependencies.Kotlinx.Coroutines.core)
    // Kotlinx serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0-RC")
    // Kotlinx datetime
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.androidExt)
    //implementation(Dependencies.Koin.androidCompose)
    //implementation(Dependencies.Koin.androidWorkManager)
    //    implementation(Libs.Koin.androidExt)
    // Timber
    //implementation(Dependencies.Timber.timber)
    // OkLog3
    //implementation(Dependencies.OkLog3.okLog3)
    // Accompanist
    //implementation(Dependencies.Accompanist.insets)
    // Landscapist
    implementation("com.github.skydoves:landscapist-coil:1.1.5")
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
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.caldeirasoft.outcast"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
        // Required when setting minSdkVersion to 20 or lower
        multiDexEnabled = true
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerVersion = "1.4.30"
        kotlinCompilerExtensionVersion = "1.0.0-alpha12"
    }
    buildToolsVersion = "30.0.3"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs += listOf(
            "-Xallow-jvm-ir-dependencies",
            "-Xopt-in=kotlin.RequiresOptIn",
            "-Xopt-in=androidx.compose.foundation.lazy.ExperimentalLazyDsl",
            "-Xskip-prerelease-check",
            "-Xallow-unstable-dependencies",
            "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}
