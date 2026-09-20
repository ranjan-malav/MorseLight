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
# --- MorseLight keep rules (Phase 6) ---

# Firebase Crashlytics: keep line numbers + source file for readable stack traces
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Firebase / Google services use reflection on model classes
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# CameraX ships its own consumer rules; guard against optional deps
-dontwarn com.google.auto.value.**

# Kotlin coroutines internals
-dontwarn kotlinx.coroutines.**

# App data classes read via DataStore/Compose state (defensive; Compose needs no rules)
-keep class com.ranjan.malav.morselight_flashlightwithmorsecode.data.** { *; }
