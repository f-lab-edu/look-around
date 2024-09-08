plugins {
    id("lookaround.android.library")
}

android {
    namespace = "kky.flab.lookaround.core.database"
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.aac.room)
    implementation(libs.aac.room.ktx)
    ksp(libs.aac.roomCompiler)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    ksp(libs.moshi.codegen)
}