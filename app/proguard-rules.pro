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
