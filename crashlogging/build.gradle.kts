plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.gravatar.crashlogging"
    buildFeatures.buildConfig = true
}

dependencies {
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    api(libs.automattic.crashlogging)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.core.ktx)
}
