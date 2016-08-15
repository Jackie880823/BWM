# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in F:\windows\ProgramFiles-green\adt-bundle-windows-x86_64-20131030\sdk/tools/proguard/proguard-android.txt
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

# 保持源文件和行号的信息,用于混淆后定位错误位置
-keepattributes SourceFile,LineNumberTable

-optimizationpasses 5

#包明不混合大小写
-dontusemixedcaseclassnames

#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses

 #优化  不优化输入的类文件
-dontoptimize

 #预校验
-dontpreverify

 #混淆时是否记录日志
-verbose

 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#保护注解
-keepattributes *Annotation*

#避免混淆泛型 如果混淆报错建议关掉
-keepattributes Signature

#-printmapping out.map
#-renamesourcefileattribute SourceFile
#-keepattributes Exceptions,SourceFile

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.MultiDexApplication
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgent
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.support.v4.app.DialogFragment
-keep public class * extends android.app.Fragment
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
 native <methods>;
}

-keep public class * extends android.view.View {
 public <init>(android.content.Context);
 public <init>(android.content.Context, android.util.AttributeSet);
 public <init>(android.content.Context, android.util.AttributeSet, int);
 public void set*(...);
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

-keepclassmembers class **.R$* {
 public static <fields>;
}

-dontwarn android.support.**
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

# Matter Design支持库
-keep android.support.design**
-keep class android.support.design.internal.**{*;}
-keep interface android.support.design.internal.**{*;}
-keep class android.support.design.widget.**{*;}
-keep interface android.support.design.widget.**{*;}

# 图片加载库
-dontwarn com.nostra13.universalimageloader.core.**
-keep class com.nostra13.universalimageloader.core.**
-keepclassmembers class com.nostra13.universalimageloader.core.** { *; }


#pinyin4j
-dontwarn net.soureceforge.pinyin4j.**
-dontwarn demo.**
#-keep class net.sourceforge.pinyin4j.** { *;}
#-keep class demo.** { *;}

#gson
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.examples.android.model.** { *; }

#volley
#-keep class com.android.volley.** {*;}
#-keep class com.android.volley.toolbox.** {*;}
#-keep class com.android.volley.Response$* { *; }
#-keep class com.android.volley.Request$* { *; }
#-keep class com.android.volley.RequestQueue$* { *; }
#-keep class com.android.volley.toolbox.HurlStack$* { *; }
#-keep class com.android.volley.toolbox.ImageLoader$* { *; }

#apache http
-dontwarn org.apache.http.entity.mime.**
-dontwarn org.apache.http.**
-keep class org.apache.http.** { *; }
-keep class org.apache.http.entity.mime.** { *;}

#jiguang
-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

#百度map
-dontwarn com.baidu.**
-keep class com.baidu.** { *; }
-keep class vi.com.gdi.bgl.android.** { *; }

-dontwarn com.j256.ormlite.**
#-keep com.j256.ormlite.** { *; }

#xmpp asmack

#Only if the above haven't fixed then, go with keeping your db class files and its members.
#In your case, that would be

#entity
-dontwarn com.madxstudio.co8.entity.**
-keep class com.madxstudio.co8.entity.**
-keepclassmembers class com.madxstudio.co8.entity.** { *; }
-keep class com.madxstudio.co8.db.SQLiteHelperOrm
-keepclassmembers class com.madxstudio.co8.db.SQLiteHelperOrm { *; }
-dontwarn com.artifex.mupdfdemo.**
-keep class com.artifex.mupdfdemo.**
-keepclassmembers class com.artifex.mupdfdemo.** { *; }

#java mail
   -dontwarn javax.activation.**
    -dontwarn javax.security.**
    -dontwarn java.awt.**
    -libraryjars <java.home>/lib/rt.jar
    -keep class javax.** {*;}
    -keep class com.sun.** {*;}
    -keep class myjava.** {*;}
    -keep class org.apache.harmony.** {*;}
    -keep public class Mail {*;}


#piwik
-dontwarn org.piwik.sdk.**
-keep class org.piwik.sdk.** {*;}
-keepclassmembers class org.piwik.sdk.** { *; }

-dontwarn javax.annotation.**


-keepclassmembers class ** {
    public void onEvent*(***);
}

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

#小红点
-keep class me.leolin.shortcutbadger.** {*;}
-keep public class pl.droidsonroids.gif.GifIOException{<init>(int);}