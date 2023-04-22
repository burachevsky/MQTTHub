buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://repo.eclipse.org/content/repositories/paho-snapshots/")
        maven(url = "https://www.jitpack.io")
        jcenter()
        maven(url = "https://repo.spring.io/plugins-release/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.ANDROID_GRADLE_PLUGIN}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.20")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.NAVIGATION}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven(url = "https://repo.eclipse.org/content/repositories/paho-snapshots/")
        maven(url = "https://www.jitpack.io")
        maven(url = "https://repo.spring.io/plugins-release/")
    }
}
