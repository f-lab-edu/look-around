plugins {
    id("lookaround.android.library")
}

android {
    namespace = "kky.flab.lookaround.core.ui_navigation"
}

dependencies {
    implementation(projects.feature.home)
    implementation(projects.feature.record)
    implementation(projects.feature.setting)

    implementation(libs.android.navigation.fragment)
    implementation(libs.android.navigation.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}