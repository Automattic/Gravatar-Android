pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            setUrl("https://a8c-libs.s3.amazonaws.com/android")
            content {
                includeGroup("com.automattic")
                includeGroup("com.automattic.tracks")
                includeGroup("com.automattic.ucrop")
            }
        }
    }
}

rootProject.name = "Gravatar"
include(":analytics")
include(":app")
include(":homeUi")
include(":loginUi")
include(":testUtils")
include(":userComponent")
include(":foundations")
include(":clock")
include(":design")
include(":networkMonitor")
include(":api")
include(":crashlogging")
