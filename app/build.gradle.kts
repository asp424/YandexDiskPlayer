plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    //id("dev.shreyaspatil.compose-compiler-report-generator") version "1.0.0-beta03"
}

android {
    compileSdk = 33
    defaultConfig {
        applicationId = "com.lm.yandexdiskplayer"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "0.0.1"
        manifestPlaceholders["YANDEX_CLIENT_ID"] = "cca53e50abd046cea6f2a0b60494c693"
    }

    namespace = "com.lm.yandexdiskplayer"

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.compose.activity)
    implementation(libs.compose.foundation)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.util)
    implementation(libs.compose.material3)
    implementation(libs.compose.ripple)
    implementation(libs.compose.icons)
    implementation(libs.compose.extended)
    implementation(projects.yandexapi)
    implementation(libs.yandex.disk)
    implementation(projects.core)
    implementation(libs.converterGson.retrofit)
}