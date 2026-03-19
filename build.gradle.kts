plugins {
    id("com.android.application") version "8.7.3" apply false
    id("com.android.library") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
    id("com.google.dagger.hilt.android") version "2.52" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21" apply false
}

ext {
    set("compileSdkVersion", 35)
    set("targetSdkVersion", 28)
    set("minSdk", 21)
    set("versionCode", 400)
    set("versionName", "4.0.0")
    set("xVersion", "1.1.0")
    set("hiddenApiBypass", "4.3")
}
