buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://repo.eclipse.org/content/repositories/paho-snapshots/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.KOTLIN}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://repo.eclipse.org/content/repositories/paho-snapshots/")
    }
}
