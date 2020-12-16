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
    }
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
