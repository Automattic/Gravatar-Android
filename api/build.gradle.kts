plugins {
    id("java-library")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
}
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    dependencies {
        implementation(project(":foundations"))

        implementation(project.dependencies.platform(libs.koin.bom))
        implementation(libs.koin.core)
        implementation(libs.kotlinx.coroutines)
        implementation(libs.retrofit)
        implementation(libs.retrofit.serialization)
        ksp(libs.moshi.kotlin.codegen)
        
        // Test dependencies
        testImplementation(libs.junit)
    }
}
kotlin {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}
