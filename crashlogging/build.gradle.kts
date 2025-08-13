import java.util.Properties

plugins {
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.kotlin.android)
}

fun secretsProperties(): Properties {
    return rootProject.extra["secretsProperties"] as Properties
}

android {
    namespace = "com.gravatar.crashlogging"
    buildFeatures.buildConfig = true

    defaultConfig {
        buildConfigField(
            "String",
            "SENTRY_DSN",
            "\"${secretsProperties()["sentryDsn"]?.toString() ?: ""}\"",
        )
    }
}

dependencies {
    implementation(project(":userComponent"))

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.androidx.core.ktx)

    api(libs.automattic.crashlogging)
}
