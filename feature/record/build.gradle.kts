plugins {
    id("lookaround.android.feature")
}

android {
    namespace = "kky.flab.lookaround.feature.record"

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(libs.landscapist.bom)
    implementation(libs.landscapist.coil)
}
