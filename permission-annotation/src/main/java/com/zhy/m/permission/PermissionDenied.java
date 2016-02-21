package com.zhy.m.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by zhy on 16/2/19.
 */
@Target(ElementType.METHOD)
public @interface PermissionDenied
{
    int value();
}
