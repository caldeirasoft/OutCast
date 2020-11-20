import org.jetbrains.kotlin.kapt3.base.Kapt.kapt

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    kotlin("plugin.serialization") version Versions.kotlin
    id("com.squareup.sqldelight")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.appcompat)
    // LifeCycle
    implementation(Libs.AndroidX.LifeCycle.lifecycle)
    implementation(Libs.AndroidX.LifeCycle.viewModel)
    // Compose
    implementation(Libs.AndroidX.Compose.runtime)
    implementation(Libs.AndroidX.Compose.foundation)
    implementation(Libs.AndroidX.Compose.layout)
    implementation(Libs.AndroidX.Compose.ui)
    implementation(Libs.AndroidX.Compose.material)
    implementation(Libs.AndroidX.Compose.animation)
    implementation(Libs.AndroidX.Compose.iconsExtended)
    implementation(Libs.AndroidX.Compose.tooling)
    // Navigation
    implementation(Libs.AndroidX.Navigation.compose)
    // Paging
    implementation(Libs.AndroidX.Paging.compose)
    // Hilt
    implementation(Libs.AndroidX.Hilt.hilt)
    implementation(Libs.AndroidX.Hilt.viewModel)
    kapt(Libs.AndroidX.Hilt.viewModelCompiler)
    kapt(Libs.AndroidX.Hilt.compiler)
    // DataStore
    implementation(Libs.AndroidX.DataStore.datastore)
    implementation(Libs.AndroidX.DataStore.preferences)
    // Kotlin coroutines
    implementation(Libs.Coroutines.core)
    // Kotlinx serialization
    implementation(Libs.Kotlinx.serialization)
    // Kotlinx datetime
    implementation(Libs.Kotlinx.datetime)
    // Koin
    implementation(Libs.Koin.core)
    implementation(Libs.Koin.coreExt)
    implementation(Libs.Koin.androidScope)
    //implementation(Libs.Koin.androidCompose)
    implementation(Libs.Koin.androidViewModel)
    // Ktor client
    implementation(Libs.Ktor.clientCore)
    implementation(Libs.Ktor.encoding)
    implementation(Libs.Ktor.serialization)
    implementation(Libs.Ktor.clientAndroid)
    // Landscapist
    implementation(Libs.Landscapist.coil)
    // SQLDelight
    implementation(Libs.SqlDelight.runtime)
    implementation(Libs.SqlDelight.coroutines)
    implementation(Libs.SqlDelight.androidDriver)
}
android {
    compileSdkVersion(30)
    defaultConfig {
        applicationId = "com.caldeirasoft.outcast"
        minSdkVersion(24)
        targetSdkVersion(30)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Libs.AndroidX.Compose.version
        kotlinCompilerVersion = Libs.Kotlin.version
    }
}

sqldelight {
    database("Database") {
        packageName = "com.caldeirasoft.outcast"
        schemaOutputDirectory = file("build/dbs")
        dialect = "sqlite:3.24"
    }
}