plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.github.burachevsky.mqtthub.feature.settings"
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
    implementation(project(":core:eventbus"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))

    implementation(libs.material)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.viewbindingpropertydelegate.noreflection)
}