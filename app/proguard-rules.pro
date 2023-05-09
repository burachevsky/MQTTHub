# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class * implements android.os.Parcelable
-keep class * implements java.io.Serializable

-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type

-keep class com.github.burachevsky.mqtthub.** { *; }
-keep class com.github.burachevsky.mqtthub.core.model.** { *; }
-keep class com.github.burachevsky.mqtthub.core.mqtt.** { *; }
-keep class com.github.burachevsky.mqtthub.core.db.** { *; }
-keep class com.github.burachevsky.mqtthub.core.db.entity.** { *; }
-keep class com.github.burachevsky.mqtthub.core.db.dao.** { *; }
-keep class com.github.burachevsky.mqtthub.core.data.mapper.** { *; }
-keep class com.github.burachevsky.mqtthub.core.data.preferences.** { *; }
-keep class com.github.burachevsky.mqtthub.core.common.Converter
-keep class org.eclipse.paho.client.mqttv3.** { *; }

-keepattributes Signature
-keepattributes Annotation

-dontwarn javax.lang.model.SourceVersion
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.ElementKind
-dontwarn javax.lang.model.element.Modifier
-dontwarn javax.lang.model.type.TypeMirror
-dontwarn javax.lang.model.type.TypeVisitor
-dontwarn javax.lang.model.util.SimpleTypeVisitor8