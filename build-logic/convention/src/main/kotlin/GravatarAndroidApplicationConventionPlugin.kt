import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.AppPlugin
import com.gravatar.app.configureDetekt
import com.gravatar.app.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

const val TARGET_SDK = 35
const val COMPILE_SDK = 35
const val MIN_SDK = 24
private const val APP_ID = "com.gravatar.app"

class GravatarAndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<AppPlugin>()
            apply<KotlinAndroidPluginWrapper>()

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this)
                configureCompose(this)
                defaultConfig.apply {
                    applicationId = APP_ID
                    targetSdk = TARGET_SDK
                    versionCode = project.property("versionCode") as Int
                    versionName = project.property("versionName") as String
                }
                buildFeatures.buildConfig = true
                configureBuildTypes(this@with)
                lint {
                    sarifReport = true
                    checkDependencies = true
                }
            }
            configureDetekt()
        }
    }

    private fun ApplicationExtension.configureBuildTypes(project: Project) {
        val secretsProperties = project.property("secretsProperties") as java.util.Properties
        val secretsPath = project.property("secretsPath") as String
        val canSignRelease = secretsProperties.isNotEmpty()

        if (!canSignRelease) {
            project.logger.warn("Release signing configuration skipped: no secrets properties found")
        }

        signingConfigs {
            maybeCreate("debug").apply {
                storeFile = project.rootProject.file("debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }

            if (canSignRelease) {
                maybeCreate("release").apply {
                    storeFile = project.file("$secretsPath/${secretsProperties.getProperty("uploadStoreFile")}")
                    storePassword = secretsProperties.getProperty("uploadStorePassword")
                    keyAlias = secretsProperties.getProperty("uploadKeyAlias")
                    keyPassword = secretsProperties.getProperty("uploadKeyPassword")
                }
            }
        }

        buildTypes {
            getByName("debug") {
                signingConfig = signingConfigs.getByName("debug")
            }
            getByName("release") {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
                // Only set release signing config if it was created
                signingConfigs.findByName("release")?.let { releaseSigningConfig ->
                    signingConfig = releaseSigningConfig
                }
            }
        }
    }
}
