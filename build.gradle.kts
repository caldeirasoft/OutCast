buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", version = Dependencies.Kotlin.version))
        classpath(Dependencies.androidGradlePlugin)
        classpath(Dependencies.SquareUp.SqlDelight.gradlePlugin)
    }
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
