import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    kotlin("jvm") version "1.4.10"
}

repositories {
    google()
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}
