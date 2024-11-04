plugins {
    id("lookaround.android.library")
    id("lookaround.android.compose")
    id("kotlinx-serialization")
}

android {
    namespace = "kky.flab.lookaround.core.ui_navigation"
}

dependencies {
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.android.navigation.fragment)
    implementation(libs.android.navigation.ui)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
