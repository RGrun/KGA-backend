
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.plugin.serialization)

    id("org.jlleitschuh.gradle.ktlint")
}

group = "guru.furu.kgaBackend.adapter"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(project(":domain"))
    implementation(project(":client"))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.0")

    implementation("org.neo4j.driver:neo4j-java-driver:5.27.0")

    implementation("net.coobird:thumbnailator:0.4.20")

    implementation(kotlin("reflect"))

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
