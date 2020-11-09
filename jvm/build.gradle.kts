import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
}
group = "me.eoa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
    maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
    maven(url = uri("https://dl.bintray.com/ekito/koin/")) // koin
}
dependencies {
    testImplementation(kotlin("test-junit"))
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}