package kr.co.namee.permissiongen_sample;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import kr.co.namee.permissiongen.BuildConfig;

/**
 * Created by namee on 2014. 11. 13..
 */
public class Dlog {
  public static final String TAG = "NAMEE";
  public static final String OS = "AND";

  public static void debug(String str) {
    //if (!BuildConfig.DEBUG) return;
    Log.d(TAG, buildLogMsg(str));
  }

  public static void debug(String str, Throwable t) {
    if (!BuildConfig.DEBUG) return;
    Log.d(TAG, buildLogMsg(str), t);
  }

  public static void error(String str) {
    if (!BuildConfig.DEBUG) return;
    Log.e(TAG, buildLogMsg(str));
  }

  public static void error(String str, Throwable t) {
    if (!BuildConfig.DEBUG) return;
    Log.e(TAG, buildLogMsg(str), t);
  }

  /**
   * 앱 버젼
   *
   * @return version
   */
  public static int getAppVersion(Context context) {
    try {
      PackageInfo packageInfo =
          context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      return packageInfo.versionCode;
    } catch (PackageManager.NameNotFoundException e) {
      throw new RuntimeException("Could not get package name: " + e);
    }
  }

  /**
   * 디바이스 고유값
   */
  public static String getDeviceSerialNumber() {
    try {
      return (String) Build.class.getField("SERIAL").get(null);
    } catch (Exception ignored) {
      return null;
    }
  }

  public static String buildLogMsg(String message) {

    StackTraceElement ste = Thread.currentThread().getStackTrace()[4];

    StringBuilder sb = new StringBuilder();

    sb.append("[");
    sb.append(ste.getFileName().replace(".java", ""));
    sb.append("::");
    sb.append(ste.getMethodName());
    sb.append("]");
    sb.append(message);

    return sb.toString();
  }
}
