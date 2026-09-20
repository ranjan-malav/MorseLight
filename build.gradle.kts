// Top-level build file. Plugins are declared here (apply false) and applied in :app.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
}
