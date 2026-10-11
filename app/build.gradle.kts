import java.util.Properties

// Release signing reads a gitignored keystore.properties when present (see keystore.properties.template).
// Without it, assembleRelease produces an unsigned APK — fine for CI and local R8 testing.
val keystorePropsFile = rootProject.file("keystore.properties")
val keystoreProps = Properties().apply {
    if (keystorePropsFile.exists()) keystorePropsFile.inputStream().use { load(it) }
}
val hasKeystore = keystorePropsFile.exists()

plugins {
    alias(libs.plugins.android.application)
    // Kotlin is provided by AGP's built-in Kotlin support (no kotlin-android plugin).
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.ranjan.malav.morselight_flashlightwithmorsecode"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.ranjan.malav.morselight_flashlightwithmorsecode"
        minSdk = 26
        targetSdk = 36
        versionCode = 13
        versionName = "4.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        if (hasKeystore) {
            create("release") {
                storeFile = file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            if (hasKeystore) signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Package native debug symbols so Play can symbolicate native crashes/ANRs. NOTE: this
            // app has no native code of its own, and the bundled .so files (CameraX/DataStore/…)
            // ship pre-stripped with no symbol table, so nothing is currently extracted and Play's
            // "no debug symbols" warning is expected + harmless. Kept as the recommended setting so
            // symbols are captured automatically if native code is ever added.
            ndk { debugSymbolLevel = "FULL" }
        }
    }

    buildFeatures {
        compose = true
        // ViewBinding removed with the old fragment UI (Phase 4). Old layouts are no longer
        // inflated; their XML + custom-view attrs are deleted in the Phase 5 resource cleanup.
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
// Play Store CLI releases are handled by fastlane `supply` (see fastlane/), not a Gradle plugin:
// Gradle Play Publisher doesn't support AGP 9 yet (it expects the removed BaseAppModuleExtension).

dependencies {
    // Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.compose.ui.tooling)

    // AndroidX / Material
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // CameraX
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}
