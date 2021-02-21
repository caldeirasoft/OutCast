import de.fayard.refreshVersions.bootstrapRefreshVersions

rootProject.name = "OutCast"
include(":shared")
include(":androidApp")
include(":js")
include(":jvm")
enableFeaturePreview("GRADLE_METADATA")
include(":shared_module")

fun propertiesFromFile(loadedFile: File): java.util.Properties {
    return java.util.Properties().apply {
        if (loadedFile.isFile) {
            java.io.FileInputStream(loadedFile).use {
                load(it)
            }
        }
    }
}

pluginManagement {
    repositories {
        google()
        jcenter()
        mavenCentral()
        gradlePluginPortal()
    }

    val versionsProperties = rootDir.parentFile.resolve("versions.properties").let {
        java.util.Properties().apply {
            if (it.isFile) {
                java.io.FileInputStream(it).use {
                    load(it)
                }
            }
        }
    }
    val kotlinVersion = versionsProperties.getProperty("version.kotlin")
    //val kotlinVersion = "1.4.21-2"
    plugins {
        kotlin("multiplatform") apply false
        kotlin("plugin.serialization") version kotlinVersion apply false
    }
}

buildscript {
    repositories { gradlePluginPortal() }
    dependencies.classpath("de.fayard.refreshVersions:refreshVersions:0.9.7")
}

bootstrapRefreshVersions(
    extraArtifactVersionKeyRules = listOf(
        file("refreshVersions-extra-rules.txt").readText()
    )
)

bootstrapRefreshVersions()


