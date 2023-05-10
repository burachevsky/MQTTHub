plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    kotlin("kapt")
}

android {
    namespace = "com.github.burachevsky.mqtthub.core.ui"
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        minSdk = Versions.MIN_SDK
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    api(project(":core:event-bus"))
    api(project(":core:common"))
    implementation(project(":core:model"))

    api(libs.androidx.core)
    api(libs.androidx.appcompat)
    api(libs.androidx.lifecycle.livedata)
    api(libs.androidx.lifecycle.runtime)
    api(libs.androidx.lifecycle.compiler)
    api(libs.androidx.navigation.fragment)
    api(libs.androidx.navigation.ui)
    api(libs.androidx.activity)
    api(libs.androidx.appcompat)
    api(libs.androidx.fragment)
    api(libs.material)
    api(libs.viewbindingdelegate)
    api(kotlin("reflect"))
    api(libs.mpandroidchart)
    api(libs.androidx.drawerlayout)
    api(libs.rotatelayout)
    api(libs.timber)
    kapt(libs.dagger.compiler)
}
