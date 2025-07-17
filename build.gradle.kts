plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1"
}

ktlint {
    version.set("1.2.1")
    android.set(true)
    outputColorName.set("RED")
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}
