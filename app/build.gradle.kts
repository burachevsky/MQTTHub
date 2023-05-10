plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.github.burachevsky.mqtthub"
    compileSdk = Versions.COMPILE_SDK

    defaultConfig {
        applicationId = "com.github.burachevsky.mqtthub"
        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK
        versionCode = Versions.VERSION_CODE
        versionName = Versions.VERSION_NAME

        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }

        debug {
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    sourceSets {
        getByName("debug") {
            java.srcDir("src/debugRelease/java")
        }
        getByName("release") {
            java.srcDir("src/debugRelease/java")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    packagingOptions {
        resources.excludes += "META-INF/AL2.0"
        resources.excludes += "META-INF/LGPL2.1"
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:data"))
    implementation(project(":core:db"))
    implementation(project(":core:domain"))
    implementation(project(":core:event-bus"))
    implementation(project(":core:model"))
    implementation(project(":core:mqtt"))
    implementation(project(":core:preferences"))
    implementation(project(":core:ui"))

    implementation(project(":feature:add-broker"))
    implementation(project(":feature:add-tile"))
    implementation(project(":feature:brokers"))
    implementation(project(":feature:mqtt-service"))
    implementation(project(":feature:dashboards"))
    implementation(project(":feature:home"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:tile-details"))
    implementation(project(":feature:help-and-feedback"))

    kapt(libs.dagger.compiler)
}