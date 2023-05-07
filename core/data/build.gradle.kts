plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

android {
    namespace = "com.github.burachevsky.mqtthub.data"
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        minSdk = Versions.MIN_SDK
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":core:preferences"))
    implementation(project(":core:database"))

    implementation(libs.gson)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.kotlinx.coroutines.core)
}