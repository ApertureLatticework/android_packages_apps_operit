// 树内版 BuildConfig（Gradle 生成物不进树；app/src/soong/java 仅 Soong 编译）
// 版本三处同步：本文件、app/build.gradle.kts 的 versionCode/versionName、
// app/src/main/AndroidManifest.xml 的 android:versionCode/versionName（树内 APK 版本取自 manifest）。
package com.ai.assistance.operit;

public final class BuildConfig {
    public static final boolean DEBUG = false;
    public static final String BUILD_TYPE = "release";
    public static final String APPLICATION_ID = "com.ai.assistance.operit";
    public static final int VERSION_CODE = 51;
    public static final String VERSION_NAME = "1.12.2";

    private BuildConfig() {}
}
