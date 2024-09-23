import com.lookaround.app.findLibrary

plugins {
    id("lookaround.android.library")
    id("lookaround.android.compose")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(findLibrary("androidx.core.ktx"))
    implementation(findLibrary("androidx.appcompat"))
    implementation(findLibrary("androidx.activity"))
    implementation(findLibrary("androidx.constraintlayout"))
    implementation(findLibrary("androidx.core.ktx"))
    implementation(findLibrary("material"))
    implementation(findLibrary("fragment.ktx"))
    implementation(findLibrary("androidx.lifecycle.runtime.compose"))
    implementation(findLibrary("androidx.compose.navigation"))
    implementation(findLibrary("hilt.navigation.compose"))
    testImplementation(findLibrary("junit"))
    androidTestImplementation(findLibrary("androidx.junit"))
    androidTestImplementation(findLibrary("androidx.espresso.core"))
}
