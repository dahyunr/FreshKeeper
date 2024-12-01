pluginManagement {
    repositories {
        gradlePluginPortal()
        google() // Google Maven Repository
        mavenCentral() // Maven Central Repository
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
    repositories {
        google() // Google Maven Repository
        mavenCentral() // Maven Central Repository
        maven { url = uri("https://jitpack.io") } // JitPack 추가
    }
}

rootProject.name = "FreshKeeper"
include(":app")
