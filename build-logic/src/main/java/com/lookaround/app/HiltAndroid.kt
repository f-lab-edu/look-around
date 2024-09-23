package com.lookaround.app

import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

fun Project.configureHiltAndroid() {
    with(pluginManager) {
        apply("dagger.hilt.android.plugin")
        apply("com.google.devtools.ksp")
    }

    dependencies {
        "implementation"(findLibrary("hilt"))
        "ksp"(findLibrary("hiltCompiler"))
    }
}