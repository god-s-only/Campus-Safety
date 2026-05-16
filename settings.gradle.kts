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
    }
}

rootProject.name = "CampusSafety"

include(":app")
include(":core:common")
include(":core:ui")
include(":core:network")
include(":auth:domain")
include(":auth:data")
include(":auth:presentation")
include(":report:domain")
include(":report:data")
include(":report:presentation")
