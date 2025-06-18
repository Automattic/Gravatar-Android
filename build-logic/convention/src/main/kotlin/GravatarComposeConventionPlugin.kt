import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import com.gravatar.app.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradleSubplugin

class GravatarComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extensions.configure<LibraryExtension> {
                configureCompose(this)
            }
        }
    }
}

internal fun Project.configureCompose(commonExtension: CommonExtension<*, *, *, *, *, *>) {
    apply<ComposeCompilerGradleSubplugin>()
    commonExtension.apply {
        buildFeatures {
            compose = true
        }
    }
    addComposeDependencies()
}

private fun Project.addComposeDependencies() {
    dependencies {
        add("implementation", platform(libs.findLibrary("androidx.compose.bom").get()))
        // Android Studio Preview support
        add("implementation", libs.findLibrary("androidx-ui-tooling").get())
        add("debugImplementation", libs.findLibrary("androidx-ui-tooling-preview").get())
        add("debugImplementation", libs.findLibrary("androidx-ui-test-manifest").get())
    }
}
