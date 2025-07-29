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
    implementation(project(":userComponent"))
    implementation(project(":design"))
    implementation(project(":networkMonitor"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.navigation)
    implementation(libs.androidx.constraintLayout.compose)
    implementation(libs.coil.compose)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.gravatar.core)
    implementation(libs.gravatar.ui)
    implementation(libs.ucrop)
    implementation(libs.qrose)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
    testImplementation(project(":testUtils"))
}
