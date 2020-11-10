plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-android-extensions")
    kotlin("plugin.serialization") version Versions.kotlin
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
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.appcompat)
    // LifeCycle
    implementation(Libs.AndroidX.LifeCycle.lifecycleKtx)
    implementation(Libs.AndroidX.LifeCycle.viewModelKtx)
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
        kotlinCompilerExtensionVersion = "1.0.0-alpha06"
        kotlinCompilerVersion = "1.4.10"
    }
}

sqldelight {
    database("Database") {
        packageName = "com.caldeirasoft.outcast"
        schemaOutputDirectory = file("build/dbs")
        dialect = "sqlite:3.24"
    }
}