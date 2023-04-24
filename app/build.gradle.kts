plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs.kotlin")
}

kapt {
    generateStubs = true
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


    implementation("${Libs.CORE_KTX}:${Versions.CORE}")
    implementation("${Libs.APP_STARTUP}:${Versions.APPSTARTUP}")
    implementation("${Libs.TIMBER}:${Versions.TIMBER}")

    // UI
    implementation("${Libs.ACTIVITY_KTX}:${Versions.ACTIVITY}")
    implementation("${Libs.APPCOMPAT}:${Versions.APPCOMPAT}")
    implementation("${Libs.FRAGMENT_KTX}:${Versions.FRAGMENT}")
    implementation("${Libs.CARDVIEW}:${Versions.CARDVIEW}")
    implementation("${Libs.CONSTRAINT_LAYOUT}:${Versions.CONSTRAINT_LAYOUT}")
    implementation("${Libs.MATERIAL}:${Versions.MATERIAL}")
    implementation("${Libs.FLEXBOX}:${Versions.FLEXBOX}")
    implementation("${Libs.DRAWER_LAYOUT}:${Versions.DRAWER_LAYOUT_VERSION}")

    // Architecture Components
    implementation("${Libs.LIFECYCLE_LIVE_DATA_KTX}:${Versions.LIFECYCLE}")
    implementation("${Libs.LIFECYCLE_RUNTIME_KTX}:${Versions.LIFECYCLE}")
    kapt("${Libs.LIFECYCLE_COMPILER}:${Versions.LIFECYCLE}")
    implementation("${Libs.NAVIGATION_FRAGMENT_KTX}:${Versions.NAVIGATION}")
    implementation("${Libs.NAVIGATION_UI_KTX}:${Versions.NAVIGATION}")
    implementation("${Libs.ROOM_KTX}:${Versions.ROOM}")
    implementation("${Libs.ROOM_RUNTIME}:${Versions.ROOM}")
    kapt("${Libs.ROOM_COMPILER}:${Versions.ROOM}")
    implementation("${Libs.ROOM_KTX}:${Versions.ROOM}")
    implementation("${Libs.ROOM_RUNTIME}:${Versions.ROOM}")

    // Dagger
    implementation("${Libs.DAGGER}:${Versions.DAGGER}")
    kapt("${Libs.DAGGER_COMPIER}:${Versions.DAGGER}")

    // Glide
    implementation("${Libs.GLIDE}:${Versions.GLIDE}")
    kapt("${Libs.GLIDE_COMPILER}:${Versions.GLIDE}")

    // Kotlin
    implementation("${Libs.KOTLIN_STDLIB}:${Versions.KOTLIN}")
    implementation(kotlin("reflect"))

    // Instrumentation tests
    androidTestImplementation("${Libs.ESPRESSO_CORE}:${Versions.ESPRESSO}")
    androidTestImplementation("${Libs.ESPRESSO_CONTRIB}:${Versions.ESPRESSO}")
    androidTestImplementation("${Libs.EXT_JUNIT}:${Versions.EXT_JUNIT}")

    // Local unit tests
    testImplementation("${Libs.JUNIT}:${Versions.JUNIT}")

    // Solve conflicts with gson. DataBinding is using an old version.
    implementation("${Libs.GSON}:${Versions.GSON}")

    //Paho
    implementation("${Libs.PAHO}:${Versions.PAHO}")
    implementation("${Libs.PAHO_ANDROID}:${Versions.PAHO}")

    //Charts
    implementation("${Libs.AACHART_CORE}:${Versions.AACHART_CORE}")

    //RotateLayout
    implementation("${Libs.ROTATE_LAYOUT}:${Versions.ROTATE_LAYOUT}")

    //by viewBinding()
    implementation("${Libs.BY_VIEW_BINDING}:${Versions.BY_VIEW_BINDING}")
}