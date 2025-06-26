plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
}

android {
    namespace = "com.gravatar.app.testUtils"
}

dependencies {

    implementation(libs.robolectric)
    implementation(libs.androidx.ui.junit)
    implementation(libs.coil.compose)
    implementation(libs.coil.test)
    implementation(libs.roborazzi)
    implementation(libs.roborazzi.junit.rule)
}