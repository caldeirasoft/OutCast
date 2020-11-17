buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath(Libs.androidGradlePlugin)
        classpath(Libs.SqlDelight.gradlePlugin)
        classpath(Libs.Koin.gradlePlugin)
        classpath(Libs.AndroidX.Hilt.gradlePlugin)
        classpath("com.android.tools.build:gradle:4.2.0-alpha16")
    }
}
group = "com.caldeirasoft.outcast"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
