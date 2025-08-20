import java.net.URI
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
}

fun secretsProperties(): Properties {
    return rootProject.extra["secretsProperties"] as Properties
}

android {
    namespace = "com.gravatar.app.loginUi"

    buildFeatures.buildConfig = true
    defaultConfig {
        secretsProperties().let { properties ->
            buildConfigField(
                "String",
                "OAUTH_CLIENT_ID",
                "\"${properties["oauth.clientId"]?.toString() ?: ""}\"",
            )
            buildConfigField(
                "String",
                "OAUTH_REDIRECT_URI",
                "\"${properties["oauth.redirectUri"]?.toString() ?: ""}\"",
            )
            buildConfigField(
                "String",
                "OAUTH_CLIENT_SECRET",
                "\"${properties["oauth.clientSecret"]?.toString() ?: ""}\"",
            )

            val redirectUri = properties["oauth.redirectUri"]?.let { URI(it.toString()) }
            manifestPlaceholders["OAUTH_REDIRECT_URI_PATH"] = redirectUri?.path.orEmpty()
            manifestPlaceholders["OAUTH_REDIRECT_URI_HOST"] = redirectUri?.host.orEmpty()
            manifestPlaceholders["OAUTH_REDIRECT_URI_SCHEME"] = redirectUri?.scheme.orEmpty()
        }
    }
}

dependencies {
    implementation(project(":foundations"))
    implementation(project(":userComponent"))
    implementation(project(":design"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.browser)
    implementation(project.dependencies.platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk.android)
    testImplementation(libs.turbine)
    testImplementation(project(":testUtils"))
}
