plugins {
    id("lookaround.android.library")
}

android {
    namespace = "kky.flab.lookaround.core.network"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.bundles.retrofit2)
    implementation(libs.moshi.kotlin)
}
