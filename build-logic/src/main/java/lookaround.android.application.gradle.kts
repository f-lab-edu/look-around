import com.lookaround.app.configureHiltAndroid
import com.lookaround.app.configureKotlinAndroid

plugins {
    id("com.android.application")
}

configureKotlinAndroid()
configureHiltAndroid()
