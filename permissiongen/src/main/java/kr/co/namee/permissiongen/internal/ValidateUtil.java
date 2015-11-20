package kr.co.namee.permissiongen.internal;

import android.content.pm.PackageManager;

/**
 * Created by namee on 2015. 11. 17..
 */
public class ValidateUtil {
  public static boolean verifyGrants(int... grantResults) {
    for (int result : grantResults) {
      if (result != PackageManager.PERMISSION_GRANTED) {
        return false;
      }
    }
    return true;
  }

  //public static boolean verifyPermissions(String[] perissions, Method method) {
  //  Map<String, String> map = new HashMap<>();
  //  for(String p : perissions){
  //    map.put(p, p);
  //  }
  //
  //  PermissionSuccess hasPermissions = method.getAnnotation(PermissionSuccess.class);
  //
  //  String[] pems = hasPermissions.values();
  //  if(perissions.length == pems.length){
  //    boolean match = true;
  //    for(String pem : pems){
  //      if(!map.get(pem).equals(pem)){
  //        match = false;
  //      }
  //    }
  //    if(match) return true;
  //  }
  //  return false;
  //}
}
