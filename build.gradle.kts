import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.room) apply false
}

val versionProperties = loadPropertiesFromFile(file("version.properties"))

project.apply {
    extra.apply {
        set("versionName", versionProperties.getProperty("versionName", null))
        set("versionCode", versionProperties.getProperty("versionCode", null).toInt())
        set("isCi", System.getenv("CI")?.toBoolean() ?: false)
    }
}

fun loadPropertiesFromFile(inputFile: File): Properties {
    val properties = Properties()
    if (!inputFile.exists()) {
        return properties
    }
    inputFile.inputStream().use { stream ->
        properties.load(stream)
    }
    return properties
}
