allprojects {
    repositories {
        mavenCentral()
        google()
        jcenter()
        gradlePluginPortal()
        maven(url = uri("https://dl.bintray.com/ekito/koin/")) // koin
        maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
        maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
    }
}

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://kotlin.bintray.com/kotlinx")
        maven("https://dl.bintray.com/jetbrains/kotlin-native-dependencies")
        maven("https://dl.bintray.com/kotlin/kotlin-dev")
    }

    dependencies {
        classpath(Dependencies.androidGradlePlugin)
        classpath(Dependencies.SquareUp.SqlDelight.gradlePlugin)
        classpath(Dependencies.Koin.gradlePlugin)
        classpath("com.android.tools.build:gradle:7.0.0-alpha05")
        classpath(kotlin("gradle-plugin", version = Dependencies.Kotlin.version))
    }
}

repositories {
    mavenCentral()
}
