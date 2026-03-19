pluginManagement {
    repositories {
        google()
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://www.jitpack.io") }
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        maven { url = uri("https://maven.aliyun.com/repository/public") }
        maven { url = uri("https://www.jitpack.io") }
        mavenCentral()
    }
}

rootProject.name = "ApkContainer"
include(":app")
include(":Bcore")
include(":black-reflection")
include(":compiler")
