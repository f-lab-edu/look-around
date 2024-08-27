plugins {
    id("lookaround.android.library")
}

android {
    namespace = "kky.flab.lookaround.core.ui"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.google.location)
    implementation(libs.bundles.poi)
}