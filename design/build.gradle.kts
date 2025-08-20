plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
}

android {
    namespace = "com.gravatar.app.design"
}

dependencies {
    implementation(project(":analytics"))

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.compose)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.core.ktx)

    testImplementation(libs.junit)
    testImplementation(project(":testUtils"))
}
