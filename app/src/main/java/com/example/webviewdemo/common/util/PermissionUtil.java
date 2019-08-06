package com.example.webviewdemo.common.util;

import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Build;

import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.content.pm.PackageManager.GET_META_DATA;

public class PermissionUtil {
    
    /**
     * Return the permissions used in application.
     *
     * @return the permissions used in application
     */
    public static List<String> getPermissions() {
        return getPermissions(AppUtil.getContext().getPackageName());
    }
    
    /**
     * Return the permissions used in application.
     *
     * @param packageName The name of the package.
     * @return the permissions used in application
     */
    public static List<String> getPermissions(final String packageName) {
        PackageManager pm = AppUtil.getContext().getPackageManager();
        try {
            return Arrays.asList(
                    pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
                            .requestedPermissions
            );
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    /**
     * Return whether <em>you</em> have granted the permissions.
     *
     * @param permissions The permissions.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isGranted(final String... permissions) {
        for (String permission : permissions) {
            if (!isGranted(permission)) {
                return false;
            }
        }
        return true;
    }
    
    private static boolean isGranted(final String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(AppUtil.getContext(), permission);
    }
    
    public static boolean isRuntime(String permission) {
        PermissionInfo permissionInfo = getPermissionInfo(permission);
        if (permissionInfo != null) {
            return (permissionInfo.protectionLevel & PermissionInfo.PROTECTION_MASK_BASE)
                    == PermissionInfo.PROTECTION_DANGEROUS;
        }
        return false;
    }
    
    public static PermissionInfo getPermissionInfo(String permission) {
        PackageManager pm = AppUtil.getContext().getPackageManager();
        try {
            return pm.getPermissionInfo(permission, GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    
}
