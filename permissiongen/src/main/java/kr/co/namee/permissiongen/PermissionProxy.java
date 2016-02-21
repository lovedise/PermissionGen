package kr.co.namee.permissiongen;

/**
 * Created by zhy on 16/2/21.
 */
public interface PermissionProxy<T>
{
    void grant(T source, int requestCode);

    void denied(T source , int requestCode);
}
