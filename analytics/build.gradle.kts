plugins {
    alias(libs.plugins.gravatar.android.library)
}

android {
    namespace = "com.gravatar.analytics"
}

dependencies {

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.automattic.tracks)

    testImplementation(libs.junit)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockk.android)
}