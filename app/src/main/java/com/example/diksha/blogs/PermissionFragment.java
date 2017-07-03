package com.example.diksha.blogs;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

/**
 * Created by diksha on 3/7/17.
 */

public abstract class PermissionFragment extends Fragment{

    protected abstract void onPermissionGranted();

    private static DataStash dataStash = DataStash.DATA_STASH;

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    static boolean checkPermissions(Activity activity){
        boolean permissionGranted = true;

        for(String permission : requiredPermissions)
            permissionGranted = permissionGranted &&
                    ContextCompat
                            .checkSelfPermission(activity, permission)
                            != PackageManager.PERMISSION_DENIED;

        return permissionGranted;
    }

    static void requestPermissions(Activity activity, int code){
        ActivityCompat.requestPermissions(
                activity, requiredPermissions, code);
    }
}
