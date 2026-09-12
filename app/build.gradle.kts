plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
android {
    namespace = "fr.smith.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "fr.smith.android"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
    buildTypes { release { isMinifyEnabled = false } }
}
dependencies { implementation("androidx.core:core-ktx:1.15.0") }
