object AndroidConfig {
    const val applicationId = "com.caldeirasoft.outcast.androidApp"
    const val minSdk = 24
    const val compileSdk = 30
    const val targetSdk = compileSdk
    const val versionCode = 1
    const val versionName = "1.0"
}

object SqlDelight {
    const val databaseName = "Database"
    const val packageName = "com.caldeirasoft.outcast"
}

object Modules {
    const val common = ":common"
    const val dataLocal = ":data:local"
    const val dataRemote = ":data:remote"
    const val dataRepository = ":data:repository"
}

object Plugins {
    const val multiplatform = "multiplatform"
    const val androidApplication = "com.android.application"
    const val android = "android"
    const val androidLibrary = "com.android.library"
    const val kotlinAndroidExt = "kotlin-android-extensions"
    const val js = "js"
    const val jvm = "jvm"
    const val sqlDelight = "com.squareup.sqldelight"
    const val kotlinSerialization = "plugin.serialization"
}