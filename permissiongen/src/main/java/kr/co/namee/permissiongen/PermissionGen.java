package kr.co.namee.permissiongen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import kr.co.namee.permissiongen.internal.Dlog;
import kr.co.namee.permissiongen.internal.Utils;

import static kr.co.namee.permissiongen.internal.ValidateUtil.verifyGrants;
import static kr.co.namee.permissiongen.internal.ValidateUtil.verifyPermissions;

/**
 * Created by namee on 2015. 11. 17..
 */
public class PermissionGen {
  @TargetApi(value = Build.VERSION_CODES.M)
  public static Annotation inject(Activity activity) {
    Class clazz = activity.getClass();

    if (clazz.isAnnotationPresent(AllowPermissions.class)) {
      AllowPermissions anoAnnotation = (AllowPermissions)clazz.getAnnotation(AllowPermissions.class);
      String[] values = anoAnnotation.value();
      List<String> denyPermissions = new ArrayList<>();
      for(String value : values){
        if(activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED){
          denyPermissions.add(value);
        }
      }

      if(denyPermissions.size() == 0) return null;
      activity.requestPermissions(denyPermissions.toArray(new String[denyPermissions.size()]), 100);

      return anoAnnotation;
    }
    return null;
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  public static void requestPermission(Activity activity, int requestCode, String[] permissions){
    Dlog.debug("alksdjflaksdjlfj");
    if(!Utils.isOverMarshmallow()) {
      doExecuteSuccess(activity, requestCode);
      return;
    }
    List<String> deniedPermissions = Utils.findDeniedPermissions(activity, permissions);

    Dlog.debug("sample:" + deniedPermissions);

    if(deniedPermissions.size() > 0){
      activity.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
    } else {
      doExecuteSuccess(activity, requestCode);
    }
  }

  private static void doExecuteSuccess(Activity activity, int requestCode) {
    Method executeMethod = Utils.findMethodPermissionSuccessWithRequestCode(activity.getClass(),
          PermissionSuccess.class, requestCode);

    if(executeMethod != null){
      try {
        executeMethod.invoke(activity, null);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  public static void requestResult(Activity activity, int requestCode, String[] permissions,
      int[] grantResults){
    List<String> deniedPermissions = new ArrayList<>();
    for(int i=0; i<grantResults.length; i++){
      if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
        deniedPermissions.add(permissions[i]);
      }
    }

    Method executeMethod;
    if(deniedPermissions.size() > 0){
      executeMethod = Utils.findMethodPermissionFailWithRequestCode(activity.getClass(),
          PermissionFail.class, requestCode);
    } else {
      executeMethod = Utils.findMethodPermissionSuccessWithRequestCode(activity.getClass(),
          PermissionSuccess.class, requestCode);
    }

    if(executeMethod != null){
      try {
        executeMethod.invoke(activity, null);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }


  public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
      int[] grantResults){

    Class clazz = activity.getClass();
    Method[] methods = clazz.getDeclaredMethods();

    if(verifyGrants(grantResults)){
      for(Method method : methods){
        if(method.isAnnotationPresent(PermissionSuccess.class)) {
          if(verifyPermissions(permissions, method)) {
            try {
              method.invoke(activity, null);
              return;
            } catch (IllegalAccessException e) {
              e.printStackTrace();
            } catch (InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
    }
  }



}
