package kr.co.namee.permissiongen.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * Created by namee on 2015. 11. 18..
 */
final public class Utils {
  private Utils(){}

  public static boolean isOverMarshmallow() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  @TargetApi(value = Build.VERSION_CODES.M)
  public static List<String> findDeniedPermissions(Activity activity, String... permission){
    List<String> denyPermissions = new ArrayList<>();
    for(String value : permission){
      if(activity.checkSelfPermission(value) != PackageManager.PERMISSION_GRANTED){
        denyPermissions.add(value);
      }
    }
    return denyPermissions;
  }

  public static List<String> getValuesOfAnnotation() {
    return null;
  }

  //public static int findMethoRequestCodeOfMethod(Class<? extends Annotation> clazz1) {
  //  List<Method> methods = findAnnotationMethods(clazz, clazz1);
  //  return 0;
  //}

  public static List<Method> findAnnotationMethods(Class clazz, Class<? extends Annotation> clazz1){
    List<Method> methods = new ArrayList<>();
    for(Method method : clazz.getDeclaredMethods()){
      if(method.isAnnotationPresent(clazz1)){
        methods.add(method);
      }
    }
    return methods;
  }

  public static <A extends Annotation> Method findMethodPermissionFailWithRequestCode(Class clazz,
      Class<A> permissionFailClass, int requestCode) {
    for(Method method : clazz.getDeclaredMethods()){
      if(method.isAnnotationPresent(permissionFailClass)){
        if(requestCode == method.getAnnotation(PermissionFail.class).requestCode()){
          return method;
        }
      }
    }
    return null;
  }

  public static <A extends Annotation> Method findMethodPermissionSuccessWithRequestCode(Class clazz,
      Class<A> permissionSuccessClass, int requestCode) {
    for(Method method : clazz.getDeclaredMethods()){
      if(method.isAnnotationPresent(permissionSuccessClass)){
        if(requestCode == method.getAnnotation(PermissionSuccess.class).requestCode()){
          return method;
        }
      }
    }
    return null;
  }
}
