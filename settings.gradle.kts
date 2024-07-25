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
        maven("https://repository.map.naver.com/archive/maven")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repository.map.naver.com/archive/maven")
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
include(":core:datastore")
include(":feature:main")
include(":feature:home")
include(":feature:record")
include(":feature:setting")
