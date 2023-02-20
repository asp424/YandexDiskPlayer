plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.lm.yandexapi"
    compileSdk = 33

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        freeCompilerArgs += "-Xcontext-receivers"
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.yandex.auth)
    implementation(libs.yandex.disk)
    implementation(libs.compose.activity)
    implementation(projects.core)

}
