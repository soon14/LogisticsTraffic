# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Dev\Android\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# fastJson 相关的混淆规则
#-libraryjars libs/fastjson-1.2.5.jar
-dontwarn com.alibaba.fastjson.**
-keep  class com.alibaba.fastjson.**{ *;}
-keep public class * implements java.io.Serializable { *;}
#//避免混淆泛型
-keepattributes Singature
#//不混淆注释
#-keepattributes *Annotation
# bean
-keep com.zhangzy.base.http.BaseEntity{ *; }
-keep public class * extends BaseEntity{ *; }