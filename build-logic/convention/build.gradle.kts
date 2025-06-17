import org.jetbrains.kotlin.gradle.dsl.JvmTarget
plugins {
    `kotlin-dsl`
}

group = "com.gravatar.app.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.detekt.gradlePlugin)
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("gravatarAndroidApplication") {
            id = libs.plugins.gravatar.android.application.get().pluginId
            implementationClass = "GravatarAndroidApplicationConventionPlugin"
        }
    }
}
