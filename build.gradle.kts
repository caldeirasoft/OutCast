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

repositories {
    mavenCentral()
}
