pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "BeadropCamera"

include(":app")
include(":core")
include(":design")
include(":camera")
include(":sensors")
include(":gallery")
include(":editor")
include(":player")
include(":storage")
include(":ai")
include(":navigation")
include(":settings")
include(":benchmark")
