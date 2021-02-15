plugins {
    kotlin("multiplatform")
    id("com.android.library")
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
    maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
    maven(url = uri("https://dl.bintray.com/ekito/koin/")) // koin
    maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
}

android {
    configurations {
        create("androidTestApi")
        create("androidTestDebugApi")
        create("androidTestReleaseApi")
        create("testApi")
        create("testDebugApi")
        create("testReleaseApi")
    }
}

kotlin {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.jvm
        }
    }
    sourceSets {
        val stethoVersion: String by project
        val kotlinxDatetimeVersion: String by project

        val commonMain by getting {
            dependencies {
                // Kotlinx coroutines
                implementation(Dependencies.Kotlinx.Coroutines.core)
                // Kotlinx serialization
                implementation(Dependencies.Kotlinx.Serialization.serialization)
                // Kotlinx datetime
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDatetimeVersion")
                // Koin
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Koin.coreExt)
                implementation(Dependencies.Koin.Ktor)
                // Ktor client
                implementation(Dependencies.Ktor.clientCore)
                implementation(Dependencies.Ktor.clientLogging)
                implementation(Dependencies.Ktor.encoding)
                implementation(Dependencies.Ktor.serialization)
                // MultiplatformSettings
                implementation(Dependencies.MultiplatformSettings.settings)
                implementation(Dependencies.MultiplatformSettings.settingsNoArg)
                implementation(Dependencies.MultiplatformSettings.serialization)
                implementation(Dependencies.MultiplatformSettings.coroutines)
                // SQLDelight
                implementation(Dependencies.SquareUp.SqlDelight.runtime)
                implementation(Dependencies.SquareUp.SqlDelight.coroutines)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Dependencies.Kotlinx.Coroutines.core)
                implementation(Dependencies.Kotlinx.Coroutines.test)
                implementation(Dependencies.Ktor.clientMock)
                implementation(Dependencies.Ktor.encoding)
                implementation(Dependencies.Koin.test)
                implementation(Dependencies.MultiplatformSettings.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.AndroidX.coreKtx)
                implementation(Dependencies.AndroidX.appcompat)
                implementation(Dependencies.Kotlinx.Coroutines.android)
                // Ktor client
                implementation(Dependencies.Ktor.clientAndroid)
                implementation(Dependencies.Ktor.clientOkHttp)
                // SQLDelight
                implementation(Dependencies.SquareUp.SqlDelight.androidDriver)
                // DataStore
                implementation(Dependencies.AndroidX.DataStore.preferences)
                implementation(Dependencies.AndroidX.DataStore.datastore)
                // Multiplatform settings
                implementation(Dependencies.MultiplatformSettings.datastore)
                implementation(Dependencies.Ktor.clientAndroid)
                // OkHttp
                implementation(Dependencies.SquareUp.OkHttp3.okhttp)
                implementation(Dependencies.SquareUp.OkHttp3.loggingInterceptor)
                implementation(Dependencies.SquareUp.OkHttp3.dns)
                // Stetho
                implementation("com.facebook.stetho:stetho:$stethoVersion")
                implementation("com.facebook.stetho:stetho-okhttp3:$stethoVersion")
            }
        }
        val androidDebug by getting {
            dependencies {
                // Chucker
                implementation(Dependencies.Chucker.library)
            }
        }
        val androidRelease by getting {
            dependencies {
                // Chucker
                implementation(Dependencies.Chucker.libraryNoOp)
            }
        }
        val androidTest by getting
        val jvmMain by getting {
            dependencies {
                // Ktor client
                implementation(Dependencies.Ktor.clientJvm)
                implementation(Dependencies.Ktor.serializationJvm)
                // SQLDelight
                implementation(Dependencies.SquareUp.SqlDelight.sqlDriver)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Dependencies.junit)
                implementation(Dependencies.Ktor.clientMock)
            }
        }
    }
}
android {
    compileSdkVersion(30)
    defaultConfig {
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
}

sqldelight {
    database("Database") {
        packageName = "com.caldeirasoft.outcast"
        schemaOutputDirectory = file("build/dbs")
        dialect = "sqlite:3.24"
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}