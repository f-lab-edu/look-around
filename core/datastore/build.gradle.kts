plugins {
    id("lookaround.android.library")
}

android {
    namespace = "kky.flab.lookaround.core.datastore"
}

dependencies {
    implementation(projects.core.domain)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.datastore)
}
