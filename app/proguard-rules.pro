# Beadrop Camera ProGuard Rules

# Keep application class
-keep class com.beadrop.camera.BeadropApplication { *; }

# Kotlin
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Compose
-dontwarn androidx.compose.**

# Hilt
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ViewComponentManager$FragmentContextWrapper { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# CameraX
-keep class androidx.camera.** { *; }
-dontwarn androidx.camera.**

# TensorFlow Lite
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**

# MediaPipe
-keep class com.google.mediapipe.** { *; }
-dontwarn com.google.mediapipe.**

# Serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.beadrop.**$$serializer { *; }
-keepclassmembers class com.beadrop.** {
    *** Companion;
}
-keepclasseswithmembers class com.beadrop.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Coil
-dontwarn coil.**

# Media3/ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# EXIF
-keep class androidx.exifinterface.** { *; }

# Keep data classes
-keepclassmembers class com.beadrop.core.domain.model.** { *; }
-keepclassmembers class com.beadrop.storage.database.entity.** { *; }

# Enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Parcelable
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# Native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# R8 full mode compatibility
-dontwarn java.lang.invoke.StringConcatFactory
