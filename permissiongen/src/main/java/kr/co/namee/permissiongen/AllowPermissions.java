package kr.co.namee.permissiongen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by namee on 2015. 11. 17..
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME  )
public @interface AllowPermissions {
  String[] value();
}
