plugins {
    `kotlin-dsl`
}

repositories {
    google()
    jcenter()
}
dependencies {
    implementation(kotlin("stdlib-jdk8"))
}

kotlinDslPluginOptions { experimentalWarning.set(false) }
