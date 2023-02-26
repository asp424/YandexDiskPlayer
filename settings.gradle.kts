enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

val String.composeX get() = "androidx.compose.$this"

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()
        maven { setUrl("https://jitpack.io") }
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    versionCatalogs {
        create("libs") {
            version("compose-compiler", "1.4.0-alpha02")
            version("compose-icons", "1.3.1")
            version("retrofit", "2.9.0")
            version("kotlin", "1.7.21")
            version("compose", "1.4.0-alpha03")
            version("compose-material", "1.3.1")
            version("material3", "1.1.0-alpha02")
            version("yandex-auth", "2.3.0")
            version("yandex-disk", "1.03")
            version("core", "1.9.0")
            library("compose-foundation", "foundation".composeX, "foundation")
                .versionRef("compose")
            library("compose-ui", "ui".composeX, "ui").versionRef("compose")
            library("compose-ui-util", "ui".composeX, "ui-util").versionRef("compose")
            library("compose-ripple", "material".composeX, "material-ripple")
                .versionRef("compose")
            library("compose-material3", "material3".composeX, "material3")
                .versionRef("material3")
            library("compose-icons", "material".composeX, "material-icons-core")
                .versionRef("compose-material")
            library("compose-icons-added", "material".composeX, "material-icons-extended")
                .versionRef("compose-material")
            library("compose-activity", "androidx.activity", "activity-compose")
                .version("1.7.0-alpha01")
            plugin("kotlin-serialization", "org.jetbrains.kotlin.plugin.serialization")
                .versionRef("kotlin")
            library("kotlin-coroutines", "org.jetbrains.kotlinx", "kotlinx-coroutines-core")
                .version("1.6.4")
            library("yandex-auth", "com.yandex.android", "authsdk")
                .versionRef("yandex-auth")
            library("yandex-disk", "com.yandex.android", "disk-restapi-sdk")
                .versionRef("yandex-disk")
            library("core", "androidx.core", "core-ktx")
                .versionRef("yandex-disk")
            library("retrofit2-retrofit", "com.squareup.retrofit2", "retrofit")
                .versionRef("retrofit")
            library("converterGson-retrofit", "com.squareup.retrofit2", "converter-gson")
                .versionRef("retrofit")
            library("compose-extended", "material".composeX, "material-icons-extended")
                .versionRef("compose-icons")
        }
    }
}
rootProject.name = "YandexDiskPlayer"
include(":app")
include(":yandexapi")
include(":core")
