import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

fun getApiKey(propertyKey: String): String = gradleLocalProperties(rootDir, providers).getProperty(propertyKey)

plugins {
    id("lookaround.android.library")
}

android {
    namespace = "kky.flab.lookaround.core.data"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        buildConfigField("String", "WEATHER_SERVICE_KEY", getApiKey("WEATHER_SERVICE_KEY"))
    }
}

dependencies {
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.domain)
    implementation(projects.core.network)

    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}