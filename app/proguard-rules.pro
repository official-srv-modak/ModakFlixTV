# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Keep your data models for serialization/Glide
-keep class com.souravmodak.modakflixtv.Movie { *; }

# Keep Leanback presenters
-keep public class * extends androidx.leanback.widget.Presenter { *; }
-keep public class * extends androidx.leanback.widget.RowPresenter { *; }

# Keep Glide generated code if you use Glide annotations (optional but good practice)
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.module.LibraryGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl { *; }

# Prevent obfuscation of serialVersionUID for Serializable classes
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}