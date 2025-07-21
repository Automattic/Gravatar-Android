import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.gravatar.app.configureDetekt
import com.gravatar.app.configureKotlinAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

class GravatarAndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            apply<LibraryPlugin>()
            apply<KotlinAndroidPluginWrapper>()

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this)
                defaultConfig.apply {
                    consumerProguardFiles("consumer-rules.pro")
                }
                configureBuildTypes()
                lint {
                    sarifReport = true
                    checkDependencies = false
                }
            }
            configureDetekt()
        }
    }

    private fun LibraryExtension.configureBuildTypes() {
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
