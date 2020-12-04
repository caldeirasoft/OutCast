buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Libs.Kotlin.gradlePlugin)
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.SquareUp.SqlDelight.gradlePlugin)
        classpath(Libs.Koin.gradlePlugin)
    }
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
