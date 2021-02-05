buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.Kotlin.gradlePlugin)
        classpath(Dependencies.androidGradlePlugin)
        classpath(Dependencies.SquareUp.SqlDelight.gradlePlugin)
        classpath(Dependencies.Koin.gradlePlugin)
        classpath("com.android.tools.build:gradle:7.0.0-alpha05")
    }
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
