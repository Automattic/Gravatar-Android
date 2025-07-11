plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
}

android {
    namespace = "com.gravatar.app.design"
}

dependencies {
    implementation(libs.androidx.material3)
}
