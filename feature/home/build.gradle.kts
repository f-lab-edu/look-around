import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    id("lookaround.android.feature")
}

fun getApiKey(propertyKey: String): String = gradleLocalProperties(rootDir, providers).getProperty(propertyKey)

android {
    namespace = "kky.flab.lookaround.feature.home"

    val naverApiKey = getApiKey("NAVER_API_KEY")

    defaultConfig {
        buildConfigField("String", "NAVER_API_KEY", naverApiKey)
        manifestPlaceholders["naverApiKey"] = naverApiKey
    }

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(projects.feature.recording)

    implementation(libs.google.location)
}