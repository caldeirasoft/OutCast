import Versions.sqldelight
import org.jetbrains.kotlin.config.KotlinCompilerVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version Dependencies.Kotlin.version
    id("com.squareup.sqldelight")
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
    maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
    maven(url = uri("https://dl.bintray.com/ekito/koin/")) // koin
    maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
}
dependencies {
    implementation(project(":shared"))
    // Android
    implementation("com.google.android.material:material:1.2.1")
    // AndroidX
    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.AndroidX.appcompat)
    implementation(Dependencies.AndroidX.palette)
    // LifeCycle
    implementation(Dependencies.AndroidX.LifeCycle.lifecycle)
    implementation(Dependencies.AndroidX.LifeCycle.viewModel)
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
    // DataStore
    implementation(Dependencies.AndroidX.DataStore.datastore)
    implementation(Dependencies.AndroidX.DataStore.preferences)
    // Kotlin coroutines
    implementation(Dependencies.Coroutines.core)
    // Kotlinx serialization
    implementation(Dependencies.Kotlinx.serialization)
    // Kotlinx datetime
    implementation(Dependencies.Kotlinx.datetime)
    // Koin
    implementation(Dependencies.Koin.android)
    implementation(Dependencies.Koin.androidScope)
    implementation(Dependencies.Koin.androidCompose)
    implementation(Dependencies.Koin.androidViewModel)
    //    implementation(Libs.Koin.androidExt)
    // Ktor client
    implementation(Dependencies.Ktor.clientCore)
    implementation(Dependencies.Ktor.clientLogging)
    implementation(Dependencies.Ktor.encoding)
    implementation(Dependencies.Ktor.serialization)
    implementation(Dependencies.Ktor.clientAndroid)
    implementation(Dependencies.Ktor.clientOkHttp)
    // OkHttp
    implementation(Dependencies.SquareUp.OkHttp3.okhttp)
    // Accompanist
    //implementation(Dependencies.Accompanist.insets)
    // Landscapist
    implementation(Dependencies.Landscapist.coil)
    // Retrofit
    implementation(Dependencies.SquareUp.Retrofit.retrofit)
    implementation(Dependencies.SquareUp.Retrofit.KotlinXSerialization.serialization)
    // SQLDelight
    implementation(Dependencies.SquareUp.SqlDelight.runtime)
    implementation(Dependencies.SquareUp.SqlDelight.coroutines)
    implementation(Dependencies.SquareUp.SqlDelight.androidDriver)
    // Plist
    implementation(Dependencies.Plist.ddPlist)
    // Flipper
    debugImplementation(Dependencies.Facebook.Flipper.flipperDebug)
    releaseImplementation(Dependencies.Facebook.Flipper.flipperRelease)
    debugImplementation(Dependencies.Facebook.Flipper.network)
    debugImplementation(Dependencies.Facebook.Flipper.leakCanary)
    // SoLoader
    debugImplementation(Dependencies.Facebook.SoLoader.soloader)
    // SQLDelight
    implementation(Dependencies.SquareUp.SqlDelight.androidDriver)
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
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Dependencies.AndroidX.Compose.version
    }
    buildToolsVersion = "30.0.2"
}

sqldelight {
    database("Database") {
        packageName = "com.caldeirasoft.outcast"
        schemaOutputDirectory = file("build/dbs")
        dialect = "sqlite:3.24"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs = listOf(
        *kotlinOptions.freeCompilerArgs.toTypedArray(),
        "-Xallow-jvm-ir-dependencies",
        "-Xskip-prerelease-check",
        "-Xuse-experimental=kotlinx.coroutines.ExperimentalCoroutinesApi")
}
