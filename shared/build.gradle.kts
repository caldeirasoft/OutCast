plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") apply true
    id("com.android.library")
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
                api(libs.coroutines.core)
                api(libs.kotlinx.datetime)
                api(libs.kotlinx.serialization)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                api(libs.coroutines.core)
                //implementation(KotlinX.coroutines.core)
                //implementation(KotlinX.coroutines.test)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.coroutines.android)
                //implementation(KotlinX.coroutines.android)
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
                api(libs.junit)
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}