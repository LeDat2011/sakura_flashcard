# ============================================================
# SAKURA FLASHCARD - PROGUARD RULES
# Enhanced Security & Obfuscation Configuration
# ============================================================

# ==================== OPTIMIZATION ====================
# Maximum optimization passes
-optimizationpasses 5
-dontusemixedcaseclassnames
-verbose

# ==================== AGGRESSIVE OBFUSCATION ====================
# Repackage all classes into single package for harder reverse engineering
-repackageclasses 'sakura'
-allowaccessmodification
-overloadaggressively

# Rename source file attribute to hide original file names
-renamesourcefileattribute ''
-keepattributes SourceFile,LineNumberTable

# ==================== REMOVE DEBUG INFO ====================
# Remove all Log calls in release build
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
    public static int wtf(...);
}

# Remove System.out/err prints
-assumenosideeffects class java.io.PrintStream {
    public void println(...);
    public void print(...);
}

# Remove Kotlin println
-assumenosideeffects class kotlin.io.ConsoleKt {
    public static void println(...);
    public static void print(...);
}

# ==================== ANTI-DEBUG PROTECTION ====================
# Note: Actual anti-debug code should be in runtime checks
# These rules help obfuscate debug-detection code

# Hide BuildConfig
-keep class **.BuildConfig { *; }

# ==================== STRING ENCRYPTION PREPARATION ====================
# Move sensitive strings to separate class for potential encryption
# (Consider using tools like StringFog or DexGuard for actual encryption)

# ==================== LIBRARY RULES ====================

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes Annotation
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# Gson - Keep model classes for JSON serialization
-keep class com.example.sakura_flashcard.data.api.** { *; }
-keep class com.example.sakura_flashcard.data.model.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
-keep class com.google.gson.** { *; }

# Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao class *
-dontwarn androidx.room.paging.**

# Hilt/Dagger
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.android.internal.managers.ComponentSupplier { *; }
-keep class * implements dagger.hilt.internal.GeneratedComponent { *; }
-dontwarn dagger.hilt.**

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# Crypto/Security
-keep class com.google.crypto.tink.** { *; }
-keep class net.zetetic.database.** { *; }
-dontwarn net.zetetic.**

# ==================== ANDROID COMPONENTS ====================
# Keep Android components
-keep class * extends android.app.Activity
-keep class * extends android.app.Application
-keep class * extends android.app.Service
-keep class * extends android.content.BroadcastReceiver
-keep class * extends android.content.ContentProvider

# Keep Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ==================== NATIVE METHODS ====================
-keepclasseswithmembernames class * {
    native <methods>;
}

# ==================== SERIALIZATION ====================
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# ==================== ENUMS ====================
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# ==================== PARCELABLE ====================
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator CREATOR;
}

# ==================== R8 FULL MODE ====================
# Enable R8 full mode for better optimization
-allowaccessmodification
-mergeinterfacesaggressively