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
                configureBuildTypes()
                lint {
                    sarifReport = true
                    checkDependencies = true
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
                signingConfig = signingConfigs.getByName("debug")
            }
        }
    }
}
