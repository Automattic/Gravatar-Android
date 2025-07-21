plugins {
    alias(libs.plugins.gravatar.android.library)
}

android {
    namespace = "com.gravatar.app.networkmonitor"

}

dependencies {
    implementation(libs.kotlinx.coroutines)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
}