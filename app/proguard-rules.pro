# Hilt
-keepclassmembers,allowobfuscation class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Compose
-dontwarn androidx.compose.**

# Keep APK analysis models
-keep class com.apkcontainer.domain.model.** { *; }

# BlackBox
-keep class top.niunaijun.blackbox.** { *; }
-keep class me.weishu.reflection.** { *; }
-keep class black.** { *; }
-keep class mirror.** { *; }
-dontwarn top.niunaijun.**
-dontwarn black.**
-dontwarn mirror.**
-dontwarn me.weishu.**
-ignorewarnings
