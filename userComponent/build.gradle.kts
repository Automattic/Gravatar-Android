plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gravatar.app.usercomponent"
}

dependencies {
    implementation(project(":foundations"))

    implementation(libs.kotlinx.coroutines)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.ktor.core)
    implementation(libs.ktor.okhttp)
    implementation(libs.ktor.content.negotiation)
    implementation(libs.ktor.serialization.json)

    testImplementation(libs.junit)
}