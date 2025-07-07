plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.gravatar.app.usercomponent"
}

dependencies {
    implementation(project(":foundations"))

    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.datastore.prefs)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.ktor.core)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.serialization.json)
    implementation(libs.gravatar.core)

    testImplementation(libs.junit)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
    testImplementation(project(":testUtils"))
}
