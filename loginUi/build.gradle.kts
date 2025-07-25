import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.gravatar.android.library)
    alias(libs.plugins.gravatar.android.compose)
}

fun localProperties(): Properties {
    val properties = Properties()
    rootProject.file("~/.configure/gravatar-android/secrets/secrets.properties")
        .takeIf { it.exists() }
        ?.let { properties.load(FileInputStream(it)) }
        ?: logger.warn("Secrets properties file not found. Gravatar app won't work properly.")
    return properties
}

android {
    namespace = "com.gravatar.app.loginUi"

    buildFeatures.buildConfig = true
    defaultConfig {
        localProperties().let { properties ->
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
            manifestPlaceholders["OAUTH_REDIRECT_URI_HOST"] =
                properties["oauth.redirectUri"]?.toString()?.split("://")?.get(1) ?: ""
            manifestPlaceholders["OAUTH_REDIRECT_URI_SCHEME"] =
                properties["oauth.redirectUri"]?.toString()?.split("://")?.first() ?: ""
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
