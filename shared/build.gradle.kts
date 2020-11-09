plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("kotlin-android-extensions")
    kotlin("plugin.serialization") version Versions.kotlin
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    mavenCentral()
    maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
    maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
    maven(url = uri("https://dl.bintray.com/ekito/koin/")) // koin
}
kotlin {
    android()
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.jvm
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                // Kotlin
                implementation(Libs.Coroutines.core)
                // Kotlinx serialization
                implementation(Libs.Kotlinx.serialization)
                // Kotlinx datetime
                implementation(Libs.Kotlinx.datetime)
                // Koin
                implementation(Libs.Koin.core)
                implementation(Libs.Koin.coreExt)
                implementation(Libs.Koin.KTor)
                // Ktor client
                implementation(Libs.Ktor.clientCore)
                implementation(Libs.Ktor.encoding)
                implementation(Libs.Ktor.serialization)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Libs.Coroutines.core)
                implementation(Libs.Coroutines.test)
                implementation(Libs.Ktor.clientMock)
                implementation(Libs.Ktor.encoding)
                implementation(Libs.Koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Libs.AndroidX.coreKtx)
                implementation(Libs.AndroidX.appcompat)
                implementation(Libs.Coroutines.core)
                implementation(Libs.Coroutines.android)
                // Ktor client
                implementation(Libs.Ktor.clientAndroid)
                // SQLDelight
            }
        }
        val androidTest by getting
        val jvmMain by getting {
            dependencies {
                // Ktor client
                implementation(Libs.Ktor.clientJvm)
                implementation(Libs.Ktor.serializationJvm)
                // SQLDelight
                implementation(Libs.SqlDelight.sqlDriver)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Libs.junit)
                implementation(Libs.Ktor.clientMock)
            }
        }
    }
}
android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}