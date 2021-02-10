buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Dependencies.androidGradlePlugin)
        classpath(Dependencies.SquareUp.SqlDelight.gradlePlugin)
        classpath(Dependencies.Koin.gradlePlugin)
        classpath("com.android.tools.build:gradle:7.0.0-alpha05")
        classpath(kotlin("gradle-plugin", version = Dependencies.Kotlin.version))
    }
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
