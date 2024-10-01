plugins {
    id("lookaround.android.feature")
}

android {
    namespace = "kky.flab.lookaround.feature.main"

    viewBinding {
        enable = true
    }
}

dependencies {
    implementation(projects.feature.home)
    implementation(projects.feature.record)
    implementation(projects.feature.setting)
    implementation(projects.feature.recording)
    implementation(projects.core.uiNavigation)

    implementation(libs.android.navigation.fragment)
    implementation(libs.android.navigation.ui)
}