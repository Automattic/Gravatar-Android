plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
}

android {
    namespace = "com.gravatar.app.design"
}

dependencies {
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(project(":testUtils"))
}
