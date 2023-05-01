plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

kapt {
    /*generateStubs = true*/
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables.useSupportLibrary = true

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf("room.schemaLocation" to "$projectDir/schemas")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))


    implementation(libs.core.ktx)
    implementation(libs.startup)
    implementation(libs.timber)

    // UI
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.fragment.ktx)
    //implementation(libs.cardview)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    //implementation(libs.flexbox)
    implementation(libs.drawerlayout)

    // Architecture Components
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.compiler)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    kapt(libs.room.compiler)

    // Dagger
    implementation(libs.dagger)
    kapt(libs.dagger.compiler)

    // Glide
    implementation(libs.glide)
    kapt(libs.glide.compiler)

    // Kotlin
    implementation(libs.kotlin.stdlib)
    implementation(kotlin("reflect"))

    // Instrumentation tests
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.espresso.contrib)
    //androidTestImplementation(libs.junit.ext)

    // Local unit tests
    testImplementation(libs.junit)

    // Gson
    implementation(libs.gson)

    //Paho
    implementation(libs.paho)
    implementation(libs.paho.android)

    //Charts
    implementation(libs.mpandroidchart)


    //RotateLayout
    implementation(libs.rotatelayout)

    //by viewBinding()
    implementation(libs.viewbindingpropertydelegate.noreflection)
}