# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#Specifies not to ignore non-public library classes.
#-dontskipnonpubliclibraryclasses
#
##Specifies not to ignore package visible library class members
#-dontskipnonpubliclibraryclassmembers
#
#-optimizationpasses 5
##Specifies that the access modifiers of classes and class members may have become broad during processing. This can improve the results of the optimization step.
#-allowaccessmodification
##Specifies that interfaces may be merged, even if their implementing classes don't implement all interface methods. This can reduce the size of the output by reducing the total number of classes.
#-mergeinterfacesaggressively
#
##Specifies to apply aggressive overloading while obfuscating. Multiple fields and methods can then get the same names, This option can make the processed code even smaller
##-overloadaggressively
#
##Specifies to repackage all packages that are renamed, by moving them into the single given parent package
#-flattenpackagehierarchy
#
##Specifies to repackage all class files that are renamed, by moving them into the single given package. Without argument or with an empty string (''), the package is removed completely.
#-repackageclasses
#
##For example, if your code contains a large number of hard-coded strings that refer to classes, and you prefer not to keep their names, you may want to use this option
#-adaptclassstrings
##Specifies the resource files to be renamed, all resource files that correspond to class files are renamed
#-adaptresourcefilenames
#
##Specifies the resource files whose contents are to be updated. Any class names mentioned in the resource files are renamed
#-adaptresourcefilecontents
#
##Specifies not to verify the processed class files.
##-dontpreverify
#
#-verbose
#
##Specifies to print any warnings about unresolved references and other important problems, but to continue processing in any case.
#-ignorewarnings
#
## ADDED
##-dontobfuscate
##-useuniqueclassmembernames
#
## For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
#-keepclassmembers enum * {
#public static **[] values();
#public static ** valueOf(java.lang.String);
#}
#


#progauard rule file k end me ye dalen ////////////////////

-keepattributes LineNumberTable,SourceFile
#Specifies not to ignore non-public library classes.
-dontskipnonpubliclibraryclasses

#Specifies not to ignore package visible library class members
-dontskipnonpubliclibraryclassmembers

-optimizationpasses 5
#Specifies that the access modifiers of classes and class members may have become broad during processing. This can improve the results of the optimization step.
-allowaccessmodification
#Specifies that interfaces may be merged, even if their implementing classes don't implement all interface methods. This can reduce the size of the output by reducing the total number of classes.
-mergeinterfacesaggressively

#Specifies to apply aggressive overloading while obfuscating. Multiple fields and methods can then get the same names, This option can make the processed code even smaller
#-overloadaggressively

#Specifies to repackage all packages that are renamed, by moving them into the single given parent package
-flattenpackagehierarchy

#Specifies to repackage all class files that are renamed, by moving them into the single given package. Without argument or with an empty string (''), the package is removed completely.
-repackageclasses

#For example, if your code contains a large number of hard-coded strings that refer to classes, and you prefer not to keep their names, you may want to use this option
-adaptclassstrings
#Specifies the resource files to be renamed, all resource files that correspond to class files are renamed
# pro Add Change
-adaptresourcefilenames
#-injars  input(!com.JSBL.phoenixapp/resources/**)
#-outjars output
#-injars  input/com.JSBL.phoenixapp/resources
#-outjars output/com.JSBL.phoenixapp/resources
-adaptresourcefilenames **.properties,**.gif,**.jpg

-adaptresourcefilenames    **.properties,**.gif,**.jpg
-adaptresourcefilecontents **.properties,META-INF/MANIFEST.MF

#Specifies the resource files whose contents are to be updated. Any class names mentioned in the resource files are renamed
-adaptresourcefilecontents

#Specifies not to verify the processed class files.
#-dontpreverify

-verbose

#Specifies to print any warnings about unresolved references and other important problems, but to continue processing in any case.
-ignorewarnings

# ADDED
#-dontobfuscate
#-useuniqueclassmembernames

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
#-keepclassmembers enum * {
#public static **[] values();
#public static ** valueOf(java.lang.String);
#}

-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static *** d(...);
public static *** v(...);
public static *** i(...);
public static *** w(...);
public static *** e(...);
}
#/////////////////////////////////////////








-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static *** d(...);
public static *** v(...);
public static *** i(...);
public static *** w(...);
public static *** e(...);



}
##//////// Json Serlization

# -dontnote com.google.gson.annotations.Expose
#    -keepclassmembers class * {
#        @com.google.gson.annotations.Expose <fields>;
#    }
#
#
#    -keepclasseswithmembers,allowobfuscation,includedescriptorclasses class * {
#        @com.google.gson.annotations.Expose <fields>;
#    }
#
#    -dontnote com.google.gson.annotations.SerializedName
#    -keepclasseswithmembers,allowobfuscation,includedescriptorclasses class * {
#        @com.google.gson.annotations.SerializedName <fields>;
#    }
#
#
## GSON
# -dontnote com.google.gson.**
# # GSON TypeAdapters are only referenced in annotations so ProGuard doesn't find their method usage
# -keepclassmembers,allowobfuscation,includedescriptorclasses class * extends com.google.gson.TypeAdapter {
#     public <methods>;
# }
# # GSON TypeAdapterFactory is an interface, we need to keep the entire class, not just its members
# -keep,allowobfuscation,includedescriptorclasses class * implements com.google.gson.TypeAdapterFactory
# # GSON JsonDeserializer and JsonSerializer are interfaces, we need to keep the entire class, not just its members
# -keep,allowobfuscation,includedescriptorclasses class * implements com.google.gson.JsonDeserializer
# -keep,allowobfuscation,includedescriptorclasses class * implements com.google.gson.JsonSerializer
# # Ensure that all fields annotated with SerializedName will be kept
# -keepclassmembers,allowobfuscation class * {
#     @com.google.gson.annotations.SerializedName <fields>;
# }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------

#-keep class com.JSBL.phoenixapp.** { *; }
-keep class com.google.code.gson.** { *; }
-keep class com.google.android.** { *; }
-keep class com.google.android.** { *; }
-keep class com.google.firebase.** { *; }
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

### implementation for camer library

-keep public class com.sandrios.** { *; }

-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-dontwarn android.support.**

#Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#scope rules
-keep class com.scope.mhub.utils**

#uCrop
-dontwarn com.yalantis.ucrop**
-keep class com.yalantis.ucrop** { *; }
-keep interface com.yalantis.ucrop** { *; }

-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

-keep class com.google.**
-dontwarn com.google.**
-dontwarn sun.misc.**

-dontwarn com.samsung.**
-keep class com.samsung.** {*;}

-keep class androidx.appcompat.widget.** { *; }
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keep @kotlinx.parcelize.Parcelize public class *


# New
#Butterknife
-keep class butterknife.** { *; }
-keepnames class * { @butterknife.Bind *;}

-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# rxjava
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

# Support library
-dontwarn android.support.**
-dontwarn android.support.v4.**
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# retrofit
-dontwarn okio.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-keep class sun.misc.Unsafe { *; }
#your package path where your gson models are stored
-keep class com.abc.model.** { *; }

# Keep these for GSON and Jackson
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }

#keep otto
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

# Crashlitycs 2.+
-keep class com.crashlytics.** { *; }
-keep class com.crashlytics.android.**
-keepattributes SourceFile, LineNumberTable, *Annotation*
# If you are using custom exceptions, add this line so that custom exception types are skipped during obfuscation:
-keep public class * extends java.lang.Exception
# For Fabric to properly de-obfuscate your crash reports, you need to remove this line from your ProGuard config:
# -printmapping mapping.txt

# Picasso
-dontwarn com.squareup.okhttp.**

# Volley
-keep class com.android.volley.toolbox.ImageLoader { *; }

# OkHttp3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

# Needed for Parcelable/SafeParcelable Creators to not get stripped
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

# ormlite
-keep class com.j256.**
-keepclassmembers class com.j256.** { *; }
-keep enum com.j256.**
-keepclassmembers enum com.j256.** { *; }
-keep interface com.j256.**
-keepclassmembers interface com.j256.** { *; }
-keepclassmembers class * {
  public <init>(android.content.Context);
}


-keepclasseswithmembers class io.requery.android.database.** {
  native <methods>;
  public <init>(...);
}
-keepnames class io.requery.android.database.** { *; }
-keep public class io.requery.android.database.sqlite.SQLiteFunction { *; }
-keep public class io.requery.android.database.sqlite.SQLiteCustomFunction { *; }
-keep public class io.requery.android.database.sqlite.SQLiteCursor { *; }
-keep public class io.requery.android.database.sqlite.SQLiteDebug** { *; }
-keep public class io.requery.android.database.sqlite.SQLiteDatabase { *; }
-keep public class io.requery.android.database.sqlite.SQLiteOpenHelper { *; }
-keep public class io.requery.android.database.sqlite.SQLiteStatement { *; }
-keep public class io.requery.android.database.CursorWindow { *; }
-keepattributes Exceptions,InnerClasses


### greenDAO 3
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
    public static java.lang.String TABLENAME;
 }
 -keep class **$Properties

# If you do not use SQLCipher:
-dontwarn org.greenrobot.greendao.database.**
# If you do not use RxJava:
-dontwarn rx.**

### greenDAO 2
-keepclassmembers class * extends de.greenrobot.dao.AbstractDao {
    public static java.lang.String TABLENAME;
}
-keep class **$Properties




-keep interface org.parceler.Parcel
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }

-dontwarn java.lang.invoke.*
-keepclassmembers class com.codepath.models** { <fields>; }
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontnote okhttp3.**

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**


# Keep the class com.example.KotlinExample
-keep class com.example.KotlinExample
-keep public class TripManagementHelper


#####################

# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-verbose
-dontnote
-useuniqueclassmembernames
-printmapping mapping.txt

##### Google #####
# Databinding
-dontwarn android.databinding.**
# Dagger
-dontwarn com.google.errorprone.annotations.**
##################

##### OkHttp, Retrofit #####
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
#################

##### Moshi for JSON #####
-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
###########################

##### gRPC #####
-dontwarn android.test.**
-dontwarn com.google.common.**
-dontwarn javax.naming.**
-dontwarn okio.**
-dontwarn org.junit.**
-dontwarn org.mockito.**
-dontwarn sun.reflect.**
# Ignores: can't find referenced class javax.lang.model.element.Modifier
-dontwarn com.google.errorprone.annotations.**
##################

##### Protobuf #####
-keep class * extends com.google.protobuf.** { *; }
-dontwarn com.google.protobuf.**
####################

##### Crashlytics #####
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
#######################

##### Glide #####
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
#################

##### Wasabeef #####
# Protobuf
-keep class jp.wasabeef.data.proto.** { *; }
-keep class jp.wasabeef.data.grpc.** { *; }
##################

-keep class com.scope.mhub** { *; }
-keep interface com.scope.mhub** { *; }
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.scope.mhub.utils.TripManagementHelper public *;
}

-keepclassmembers enum * { *; }