# To enable ProGuard in your project, edit project.properties
# to define the proguard.config property as described in that file.
#
# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in ${sdk.dir}/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the ProGuard
# include property in project.properties.
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

-verbose

-dontwarn android.support.**
-dontwarn com.badlogic.gdx.backends.android.AndroidFragmentApplication
-dontwarn com.badlogic.gdx.utils.GdxBuild
-dontwarn com.badlogic.gdx.physics.box2d.utils.Box2DBuild
-dontwarn com.badlogic.gdx.jnigen.BuildTarget*
-dontwarn com.badlogic.gdx.graphics.g2d.freetype.FreetypeBuild

# Required if using Gdx-Controllers extension
-keep class com.badlogic.gdx.controllers.android.AndroidControllers

# Added by me ----->

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

-assumenosideeffects class com.badlogic.gdx.Application {
    public void debug(...);
    public void error(...);
    public void log(...);
}

-assumenosideeffects class com.badlogic.gdx.graphics.FPSLogger {
    public void log(...);
}

-assumenosideeffects class com.vpjardim.colorbeans.core.Dbg {
    public static void print(...);
    public static void err(...);
    public static void inf(...);
    public static void dbg(...);
}

# TODO: find why it's not working without this line
# This prevent obfuscation in all gdx package because it was throwing class not found exception
-keep class com.badlogic.gdx.** {*;}

-dontwarn com.esotericsoftware.kryo.**
-dontwarn org.objenesis.instantiator.sun.**
