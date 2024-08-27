import com.lookaround.app.configureHiltAndroid
import com.lookaround.app.libs

plugins {
    id("lookaround.android.library")
}

dependencies {
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    val libs = project.extensions.libs
    implementation(libs.findLibrary("androidx.core.ktx").get())
    implementation(libs.findLibrary("androidx.appcompat").get())
    implementation(libs.findLibrary("androidx.activity").get())
    implementation(libs.findLibrary("androidx.constraintlayout").get())
    implementation(libs.findLibrary("androidx.core.ktx").get())
    implementation(libs.findLibrary("material").get())
    implementation(libs.findLibrary("fragment.ktx").get())
    testImplementation(libs.findLibrary("junit").get())
    androidTestImplementation(libs.findLibrary("androidx.junit").get())
    androidTestImplementation(libs.findLibrary("androidx.espresso.core").get())
}