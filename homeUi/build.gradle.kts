plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gravatar.app.homeUi"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.navigation)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    testImplementation(libs.junit)
    testImplementation(project(":testUtils"))
}
