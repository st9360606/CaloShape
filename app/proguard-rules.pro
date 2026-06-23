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

# 保�?你�?資�?模�?（若??@SerializedName�?
-keep class com.caloshape.** { *; }
-keepattributes Signature, *Annotation*

# ?��??��?些�?必�??�警??
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Hilt / Dagger
-dontwarn dagger.hilt.internal.**
-dontwarn dagger.hilt.android.internal.**
-keep class dagger.hilt.** { *; }
-keep class dagger.** { *; }
-keep class javax.inject.** { *; }

# Retrofit / OkHttp / Okio
-dontwarn okio.**
-dontwarn javax.annotation.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# DTO ?�未�?@SerializedName 標註，�??��?位�?依�???package 調整�?
-keepclassmembers class com.caloshape.app.net.** { <fields>; }
-keepclassmembers class com.caloshape.app.data.** { <fields>; }

# @Keep
-keep @androidx.annotation.Keep class * { *; }
-keepclasseswithmembers class * { @androidx.annotation.Keep *; }
