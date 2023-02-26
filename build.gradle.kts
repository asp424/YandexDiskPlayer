buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }

    dependencies {
        classpath("com.android.tools.build", "gradle", "7.3.1")
        classpath(kotlin("gradle-plugin", libs.versions.kotlin.get()))
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}