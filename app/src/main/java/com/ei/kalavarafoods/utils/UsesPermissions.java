package com.ei.kalavarafoods.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UsesPermissions {

    public static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    public static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    private Activity mActivity;

    public UsesPermissions(Activity activity){
        mActivity = activity;
    }

    public boolean isPermissionGranted (String permission){
        int permissionCheck = ContextCompat.checkSelfPermission(mActivity, permission);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(String permission, int requestCode){
        ActivityCompat.requestPermissions(mActivity, new String[]{permission}, requestCode);
    }

    public void requestPermission(String[] permissions, int requestCode){
        ActivityCompat.requestPermissions(mActivity, permissions, requestCode);
    }

    public void shouldShowRequestPermissionRationale(String permission){
        ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission);
    }
}
