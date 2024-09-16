package com.lookaround.app

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureComposeAndroid() {
    with(pluginManager) {
        apply("org.jetbrains.kotlin.plugin.compose")
    }

    val libs = extensions.libs
    dependencies {
        val bom = platform(libs.findLibrary("androidx-compose-bom").get())
        "implementation"(bom)

        "implementation"(libs.findLibrary("androidx-ui").get())
        "implementation"(libs.findLibrary("androidx-material3").get())
        "implementation"(libs.findLibrary("androidx-ui-graphics").get())
        "implementation"(libs.findLibrary("androidx-ui-tooling-preview").get())
        "implementation"(libs.findLibrary("androidx-ui-tooling").get())
        "implementation"(libs.findLibrary("androidx-compose-navigation").get())
    }
}