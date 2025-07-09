plugins {
    alias(libs.plugins.gravatar.android.application)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gravatar.app"
}

dependencies {
    implementation(project(":homeUi"))
    implementation(project(":loginUi"))
    implementation(project(":analytics"))
    implementation(project(":foundations"))
    implementation(project(":userComponent"))
    implementation(project(":clock"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation)
    implementation(libs.kotlinx.coroutines)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    testImplementation(libs.junit)
    testImplementation(libs.koin.test.junit4)
    testImplementation(project(":testUtils"))
}
