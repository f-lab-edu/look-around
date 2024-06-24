pluginManagement {
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

rootProject.name = "lookaround"
include(":app")
include(":core:network")
include(":core:database")
include(":core:domain")
include(":core:data")
include(":core:ui-navigation")
include(":core:ui")
include(":feature:main")
include(":feature:home")
include(":feature:record")
include(":feature:setting")
