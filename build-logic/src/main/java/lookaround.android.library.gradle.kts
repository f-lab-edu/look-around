import com.lookaround.app.configureHiltAndroid
import com.lookaround.app.configureKotlinAndroid

plugins {
    id("com.android.library")
}

configureKotlinAndroid()
configureHiltAndroid()
