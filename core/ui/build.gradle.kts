plugins {
    id("lookaround.android.library")
    id("lookaround.android.compose")
}

android {
    namespace = "kky.flab.lookaround.core.ui"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.google.location)
    implementation(libs.bundles.poi)
}