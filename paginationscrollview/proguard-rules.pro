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

# 保留自定义 View 的类名和构造方法
-keep class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保留自定义属性
-keepclassmembers class **.R$* {
    public static <fields>;
}

# 保留特定方法（如通过反射调用）
-keep class com.chuck.paginationscrollview.view.**{*;}
-keep class com.chuck.paginationscrollview.dragndrop.**{*;}
-keep class com.chuck.paginationscrollview.annotation.**{*;}
-keep class com.chuck.paginationscrollview.builder.**{*;}
-keep class com.chuck.paginationscrollview.bean.**{*;}
-keep class com.chuck.paginationscrollview.interfaces.**{*;}
-keep class com.chuck.utils.**{*;}

# 保留注解
-keepattributes *Annotation*