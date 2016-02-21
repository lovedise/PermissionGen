package kr.co.namee.permissiongen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.internal.Utils;

import static kr.co.namee.permissiongen.internal.Utils.getActivity;

/**
 * Created by namee on 2015. 11. 17..
 * <br/>
 * modified by hongyangAndroid 2016.02.21
 */
public class PermissionGen
{
    private String[] mPermissions;
    private int mRequestCode;
    private Object object;

    private static final String SUFFIX = "$$PermissionProxy";

    private PermissionGen(Object object)
    {
        this.object = object;
    }

    public static PermissionGen with(Activity activity)
    {
        return new PermissionGen(activity);
    }

    public static PermissionGen with(Fragment fragment)
    {
        return new PermissionGen(fragment);
    }

    public PermissionGen permissions(String... permissions)
    {
        this.mPermissions = permissions;
        return this;
    }

    public PermissionGen addRequestCode(int requestCode)
    {
        this.mRequestCode = requestCode;
        return this;
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public void request()
    {
        requestPermissions(object, mRequestCode, mPermissions);
    }

    public static void needPermission(Activity activity, int requestCode, String[] permissions)
    {
        requestPermissions(activity, requestCode, permissions);
    }

    public static void needPermission(Fragment fragment, int requestCode, String[] permissions)
    {
        requestPermissions(fragment, requestCode, permissions);
    }

    public static void needPermission(Activity activity, int requestCode, String permission)
    {
        needPermission(activity, requestCode, new String[]{permission});
    }

    public static void needPermission(Fragment fragment, int requestCode, String permission)
    {
        needPermission(fragment, requestCode, new String[]{permission});
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode, String[] permissions)
    {
        if (!Utils.isOverMarshmallow())
        {
            doExecuteSuccess(object, requestCode);
            return;
        }
        List<String> deniedPermissions = Utils.findDeniedPermissions(getActivity(object), permissions);

        if (deniedPermissions.size() > 0)
        {
            if (object instanceof Activity)
            {
                ((Activity) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else if (object instanceof Fragment)
            {
                ((Fragment) object).requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), requestCode);
            } else
            {
                throw new IllegalArgumentException(object.getClass().getName() + " is not supported");
            }

        } else
        {
            doExecuteSuccess(object, requestCode);
        }
    }


    private static void doExecuteSuccess(Object activity, int requestCode)
    {
        Class clazz = activity.getClass();
        try
        {
            Class injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            PermissionProxy instance = (PermissionProxy) injectorClazz.newInstance();
            instance.grant(activity, requestCode);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    private static void doExecuteFail(Object activity, int requestCode)
    {
        Class clazz = activity.getClass();
        try
        {
            Class injectorClazz = Class.forName(clazz.getName() + SUFFIX);
            PermissionProxy instance = (PermissionProxy) injectorClazz.newInstance();
            instance.denied(activity, requestCode);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (InstantiationException e)
        {
            e.printStackTrace();
        } catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public static void onRequestPermissionsResult(Activity activity, int requestCode, String[] permissions,
                                                  int[] grantResults)
    {
        requestResult(activity, requestCode, permissions, grantResults);
    }

    public static void onRequestPermissionsResult(Fragment fragment, int requestCode, String[] permissions,
                                                  int[] grantResults)
    {
        requestResult(fragment, requestCode, permissions, grantResults);
    }

    private static void requestResult(Object obj, int requestCode, String[] permissions,
                                      int[] grantResults)
    {
        List<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++)
        {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
            {
                deniedPermissions.add(permissions[i]);
            }
        }

        if (deniedPermissions.size() > 0)
        {
            doExecuteFail(obj, requestCode);
        } else
        {
            doExecuteSuccess(obj, requestCode);
        }
    }
}
