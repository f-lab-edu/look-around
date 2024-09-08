package com.lookaround.app

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureHiltAndroid() {
    with(pluginManager) {
        apply("dagger.hilt.android.plugin")
        apply("com.google.devtools.ksp")
    }

    val libs = extensions.libs
    dependencies {
        "implementation"(libs.findLibrary("hilt").get())
        "ksp"(libs.findLibrary("hiltCompiler").get())
    }
}