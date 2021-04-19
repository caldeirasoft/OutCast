plugins {
    kotlin("js")
}
group = "me.eoa"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = uri("https://dl.bintray.com/kotlin/kotlin-eap")) //ktor
    maven(url = uri("https://kotlin.bintray.com/kotlinx/")) // kotlinx datetime
}
dependencies {
    testImplementation(kotlin("test-js"))
}
kotlin {
    js {
        browser {
            binaries.executable()
            webpackTask {
                cssSupport.enabled = true
            }
            runTask {
                cssSupport.enabled = true
            }
            testTask {
                useKarma {
                    useChromeHeadless()
                    webpackConfig.cssSupport.enabled = true
                }
            }
        }
    }
}