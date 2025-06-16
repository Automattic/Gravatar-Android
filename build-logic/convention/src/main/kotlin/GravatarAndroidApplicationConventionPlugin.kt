import com.android.build.api.dsl.ApplicationExtension
import com.android.build.gradle.AppPlugin
import com.gravatar.app.configureDetekt
import com.gravatar.app.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

private const val TARGET_SDK = 35
internal const val COMPILE_SDK = 35
internal const val MIN_SDK = 24
private const val APP_ID = "com.gravatar.app"

class GravatarAndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<AppPlugin>()
            apply<KotlinAndroidPluginWrapper>()

            extensions.configure<ApplicationExtension> {
                namespace = APP_ID
                configureKotlinAndroid(this)
                defaultConfig.apply {
                    applicationId = APP_ID
                    targetSdk = TARGET_SDK
                    versionCode = 1
                    versionName = "1.0"
                }
                configureBuildTypes()
                buildFeatures {
                    compose = true
                }
                lint {
                    sarifReport = true
                }
            }
            configureDetekt()
        }
    }

    private fun ApplicationExtension.configureBuildTypes() {
        buildTypes {
            getByName("release") {
                isMinifyEnabled = true
                proguardFiles(
                    getDefaultProguardFile("proguard-android-optimize.txt"),
                    "proguard-rules.pro",
                )
            }
        }
    }
}
