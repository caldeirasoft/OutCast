allprojects {
    repositories {
        mavenCentral()
        google()
        //jcenter()
        gradlePluginPortal()
        maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
        maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
        maven(url  = uri("https://oss.sonatype.org/content/repositories/snapshots")) {
            content {
                includeModule("com.google.dagger", "hilt-android-gradle-plugin")
            }
        }
    }
}

repositories {
    mavenCentral()
}
