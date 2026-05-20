plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.samuelcba.jarvismobile"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.samuelcba.jarvismobile"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"
    }
}
