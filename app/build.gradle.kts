import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("lookaround.android.application")
}

fun getApiKey(propertyKey: String): String = gradleLocalProperties(rootDir, providers).getProperty(propertyKey)

android {
    namespace = "kky.flab.lookaround"

    defaultConfig {
        applicationId = "kky.flab.lookaround"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val naverApiKey = getApiKey("NAVER_API_KEY")
        buildConfigField("String", "NAVER_API_KEY", naverApiKey)
    }
}

dependencies {
    implementation(projects.feature.main)
    implementation(projects.core.ui)
    implementation(projects.core.data)

    implementation(libs.naver.map)
}