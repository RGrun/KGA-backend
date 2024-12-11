repositories {
    mavenCentral()
}

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2" apply false
}

dependencies {
}
