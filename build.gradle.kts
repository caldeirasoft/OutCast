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
        val versionsProperties = rootProject.propertiesFromFile("versions.properties")
        val kotlinVersion = versionsProperties.getProperty("version.kotlin")
        classpath(kotlin("gradle-plugin", version = kotlinVersion))
        classpath(Libs.GradlePlugin.android)
        classpath(Square.SqlDelight.gradlePlugin)
    }
}

repositories {
    mavenCentral()
}
