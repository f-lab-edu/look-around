plugins {
    id("lookaround.android.feature")
}

android {
    namespace = "kky.flab.lookaround.feature.recording"

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.google.location)
    implementation(libs.naver.map)
    implementation(libs.navermap.compose)

    implementation(libs.landscapist.bom)
    implementation(libs.landscapist.coil)
}