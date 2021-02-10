plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version Dependencies.Kotlin.version
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
        val commonMain by getting {
            dependencies {
                // Kotlin
                implementation(Dependencies.Coroutines.core)
                // Kotlinx serialization
                implementation(Dependencies.Kotlinx.serialization)
                // Kotlinx datetime
                implementation(Dependencies.Kotlinx.datetime)
                // Koin
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Koin.coreExt)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(Dependencies.Coroutines.core)
                implementation(Dependencies.Coroutines.test)
                implementation(Dependencies.Koin.test)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(Dependencies.AndroidX.coreKtx)
                implementation(Dependencies.AndroidX.appcompat)
                implementation(Dependencies.Coroutines.core)
                implementation(Dependencies.Coroutines.android)
            }
        }
        val androidTest by getting
        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(Dependencies.junit)
            }
        }
    }
}
android {
    compileSdkVersion(30)
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