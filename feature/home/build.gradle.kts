plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.github.burachevsky.mqtthub.feature.home"
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

    kotlin {
        jvmToolchain(17)
    }

    sourceSets {
        getByName("debug") {
            java.srcDir("src/debugRelease/java")
        }
        getByName("release") {
            java.srcDir("src/debugRelease/java")
        }
    }

    packagingOptions {
        resources.excludes += "META-INF/AL2.0"
        resources.excludes += "META-INF/LGPL2.1"
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:database"))
    implementation(project(":core:domain"))
    implementation(project(":core:eventbus"))
    implementation(project(":core:mqtt"))

    implementation(libs.dagger)
    kapt(libs.dagger.compiler)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.drawerlayout)
    implementation(libs.viewbindingpropertydelegate.noreflection)
    implementation(libs.mpandroidchart)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compiler)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.timber)
    implementation(libs.rotatelayout)
}