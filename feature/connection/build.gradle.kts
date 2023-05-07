plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.github.burachevsky.mqtthub.feature.connection"
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
    implementation(project(":core:eventbus"))
    implementation(project(":core:mqtt"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:ui"))

    implementation(libs.core.ktx)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.kotlinx.coroutines.core)
}