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
    implementation(projects.feature.recording)
}
