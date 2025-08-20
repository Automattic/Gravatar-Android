plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.gravatar.analytics"
}

dependencies {

    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.automattic.tracks)

    testImplementation(libs.junit)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockk.android)
}