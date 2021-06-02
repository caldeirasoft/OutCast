rootProject.name = "OutCast"
include(":shared")
include(":androidApp")
include(":js")
include(":jvm")
enableFeaturePreview("VERSION_CATALOGS")

pluginManagement {
    val kotlinVersion: String by settings
    val androidGradleVersion: String by settings
    val sqldelightVersion: String by settings
    val hiltVersion: String by settings

    repositories {
        google()
        //jcenter()
        mavenCentral()
        gradlePluginPortal()
        maven(url  = uri("https://oss.sonatype.org/content/repositories/snapshots")) {
            content {
                includeModule("com.google.dagger", "hilt-android-gradle-plugin")
            }
        }
    }

    plugins {
        id("com.android.application") apply false
        kotlin("android") version kotlinVersion apply false
        kotlin("js") version kotlinVersion apply false
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        kotlin("kapt") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
        kotlin("plugin.parcelize") version kotlinVersion apply false
        id("com.squareup.sqldelight") apply false
        id("dagger.hilt.android.plugin") apply false
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> useModule("com.android.tools.build:gradle:${androidGradleVersion}")
                "com.squareup.sqldelight" -> useModule("com.squareup.sqldelight:gradle-plugin:${sqldelightVersion}")
                "dagger.hilt.android.plugin" -> useModule("com.google.dagger:hilt-android-gradle-plugin:${hiltVersion}")
                "kotlin-multiplatform" -> useModule("org.jetbrains.kotlin:kotlin-gradle-plugin:${requested.version}")
            }
        }
    }
}

dependencyResolutionManagement {
}

