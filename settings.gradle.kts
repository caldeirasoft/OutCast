rootProject.name = "OutCast"
include(":shared")
include(":androidApp")
include(":js")
include(":jvm")
enableFeaturePreview("GRADLE_METADATA")

pluginManagement {
    val kotlinVersion: String by settings
    val androidGradleVersion: String by settings
    val sqldelightVersion: String by settings

    repositories {
        google()
        //jcenter()
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.android.application") apply false
        kotlin("android") version kotlinVersion apply false
        kotlin("js") version kotlinVersion apply false
        kotlin("jvm") version kotlinVersion apply false
        kotlin("multiplatform") version kotlinVersion apply false
        kotlin("kapt") version kotlinVersion apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
        id("com.squareup.sqldelight") apply false
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> useModule("com.android.tools.build:gradle:${androidGradleVersion}")
                "com.squareup.sqldelight" -> useModule("com.squareup.sqldelight:gradle-plugin:${sqldelightVersion}")
            }
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val kotlinVersion: String by settings
            val sqldelightVersion: String by settings
            version("kotlin", kotlinVersion)
            version("desugar_jdk_libs","1.1.1")
            version("leakcanary","2.6")

            alias("coroutines-core").to("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.+")
            alias("coroutines-android").to("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.4.+")
            alias("kotlinx-datetime").to("org.jetbrains.kotlinx","kotlinx-datetime").version("0.1.1")
            alias("kotlinx-serialization").to("org.jetbrains.kotlinx","kotlinx-serialization-json").version("1.1.+")
            bundle("kotlin", listOf("coroutines-android", "kotlinx-datetime", "kotlinx-serialization"))

            alias("core-ktx").to("androidx.core:core-ktx:1.+")
            alias("appcompat").to("androidx.appcompat:appcompat:1.3.+")
            alias("palette").to("androidx.palette:palette:1.0.+")
            alias("runtime-ktx").to("androidx.lifecycle:lifecycle-runtime-ktx:2.3.+")
            alias("datastore-preferences").to("androidx.datastore:datastore-preferences:1.0.0-alpha06")

            version("compose", "1.0.0-beta01")
            alias("compose-runtime").to("androidx.compose.runtime", "runtime").versionRef("compose")
            alias("compose-foundation").to("androidx.compose.foundation", "foundation").versionRef("compose")
            alias("compose-layout").to("androidx.compose.foundation", "foundation-layout").versionRef("compose")
            alias("compose-ui").to("androidx.compose.ui", "ui").versionRef("compose")
            alias("compose-tooling").to("androidx.compose.ui", "ui-tooling").versionRef("compose")
            alias("compose-material").to("androidx.compose.material", "material").versionRef("compose")
            alias("compose-iconsExtended").to("androidx.compose.material", "material-icons-extended").versionRef("compose")
            bundle("compose", listOf(
                "compose-runtime",
                "compose-foundation",
                "compose-layout",
                "compose-ui",
                "compose-tooling",
                "compose-material",
                "compose-iconsExtended",
            ))

            alias("activity-compose").to("androidx.activity:activity-compose:1.3.0-alpha03")
            alias("viewmodel-compose").to("androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha02")
            alias("navigation-compose").to("androidx.navigation:navigation-compose:1.0.0-alpha08")
            alias("paging-compose").to("androidx.paging:paging-compose:1.0.0-alpha08")

            version("koin", "2+")
            alias("koin-androidx").to("org.koin", "koin-androidx-scope").versionRef("koin")
            alias("koin-ext").to("org.koin", "koin-androidx-ext").versionRef("koin")
            alias("koin-workmanager").to("org.koin", "koin-androidx-workmanager").versionRef("koin")
            alias("koin-compose").to("org.koin", "koin-androidx-compose").versionRef("koin")
            bundle("koin", listOf(
                "koin-androidx",
                "koin-ext",
                "koin-workmanager",
                "koin-compose",
            ))

            version("okhttp", "4.+")
            alias("okhttp-okhttp").to("com.squareup.okhttp3", "okhttp").versionRef("okhttp")
            alias("okhttp-interceptor").to("com.squareup.okhttp3", "logging-interceptor").versionRef("okhttp")
            alias("okhttp-dnsoverhttps").to("com.squareup.okhttp3", "okhttp-dnsoverhttps").versionRef("okhttp")
            // bundle is basically an alias for several dependencies
            bundle("okhttp", listOf("okhttp-okhttp", "okhttp-interceptor", "okhttp-dnsoverhttps"))

            version("retrofit", "2.+")
            alias("retrofit-core").to("com.squareup.retrofit2", "retrofit").versionRef("retrofit")
            alias("retrofit-kotlinx-serialization").to("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
            alias("retrofit-moshi").to("com.squareup.retrofit2", "converter-moshi").versionRef("retrofit")
            bundle("retrofit", listOf("retrofit-core", "retrofit-kotlinx-serialization", "retrofit-moshi"))

            version("moshi", "1.11.0")
            alias("moshi-core").to("com.squareup.moshi", "moshi").versionRef("moshi")
            alias("moshi-kotlin").to("com.squareup.moshi", "moshi-kotlin").versionRef("moshi")
            alias("moshi-kotlincodegen").to("com.squareup.moshi", "moshi-kotlin-codegen").versionRef("moshi")
            bundle("moshi", listOf("moshi-core", "moshi-kotlin"))

            version("sqldelight", sqldelightVersion)
            alias("sqldelight-android").to("com.squareup.sqldelight", "android-driver").versionRef("sqldelight")
            alias("sqldelight-coroutines").to("com.squareup.sqldelight", "coroutines-extensions").versionRef("sqldelight")
            bundle("sqldelight", listOf("sqldelight-android", "sqldelight-coroutines"))

            version("chucker", "3.4.+")
            alias("chucker-debug").to("com.github.chuckerteam.chucker", "library").versionRef("chucker")
            alias("chucker-release").to("com.github.chuckerteam.chucker", "library-no-op").versionRef("chucker")

            version("stetho", "1.5.0") // 1.5.1 has critical bug
            alias("stetho-core").to("com.facebook.stetho", "stetho").versionRef("stetho")
            alias("stetho-okhttp3").to("com.facebook.stetho", "stetho-okhttp3").versionRef("stetho")
            bundle("stetho", listOf("stetho-core", "stetho-okhttp3"))

            alias("timber").to("com.jakewharton.timber:timber:4.+")
            alias("landscapist-coil").to("com.github.skydoves:landscapist-coil:1.1.6")
            alias("accompanist-coil").to("dev.chrisbanes.accompanist:accompanist-coil:0.6.1")
            alias("junit").to("junit:junit:4.+")
        }
    }
}

