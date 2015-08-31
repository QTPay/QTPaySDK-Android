# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/qfpay/Downloads/adt-bundle-mac-x86_64-20131030/sdk/tools/proguard/proguard-android.txt
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


-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-dontwarn android.support.v4.**

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}


-keep class android_serialport_api.** {*;}
-dontwarn android_serialport_api.**

#
-dontwarn org.apache.http.entity.mime.**
-dontwarn demo.**
-keep class demo.** { *;}
-keep class org.apache.http.entity.mime.** { *;}

-dontwarn org.apache.james.mime4j.**
#
-keep class org.apache.http.** { *;}
-dontwarn org.apache.http.**


-keep class android.webkit.WebViewClient
-dontwarn android.webkit.WebViewClient

-keep class android.net.http.SslError
-dontwarn android.net.http.SslError

-keepattributes *Annotation*


-keep public class net.qfpay.king.android.R$*{
    public static final int *;
}
-keepclassmembers class * {
   public <init>(org.json.JSONObject);
}

-keepclasseswithmembernames class * {
    native <methods>;
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

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}


-keepattributes *Annotation*

-keep class * extends java.lang.annotation.Annotation { *; }




-keep class com.qfpay.sdk.** { *; }
-dontwarn com.qfpay.sdk.**

-keep class org.json.** {*;}
-keep class com.android.volley.** {*;}
-keep class com.dspread.xpos.** {*;}
-dontwarn com.dspread.xpos.**
-keep class android_serialport_api.** {*;}
-dontwarn android_serialport_api.**
-keep class com.qfpay.qpossdk.** { *; }
-keep class net.qfpay.king.android.** { *; }
-dontwarn com.qfpay.qpossdk.**,net.qfpay.king.android.util.**
-keep class com.tencent.** {*;}

#支付宝
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.auth.AlipaySDK{ public *;}
-keep class com.alipay.sdk.auth.APAuthInfo{ public *;}
-keep class com.alipay.mobilesecuritysdk.*
-keep class com.ut.*
-dontobfuscate
-dontoptimize


-keep class com.lidroid.xutils.** { *; }
-dontwarn com.lidroid.xutils.**
-keepclasseswithmembers class *{
    public *;
}





