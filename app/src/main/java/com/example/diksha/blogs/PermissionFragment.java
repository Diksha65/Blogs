package com.example.diksha.blogs;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import static com.example.diksha.blogs.CreateBlogFragment.REQUEST_PERMISSIONS;

/**
 * Created by diksha on 3/7/17.
 */

public abstract class PermissionFragment extends Fragment {

    protected abstract void onPermissionGranted();

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    static boolean checkPermissions(Activity activity) {
        boolean permissionGranted = true;

        for (String permission : requiredPermissions)
            permissionGranted = permissionGranted &&
                    ContextCompat
                            .checkSelfPermission(activity, permission)
                            != PackageManager.PERMISSION_DENIED;

        return permissionGranted;
    }

    static void requestPermissions(Activity activity, int code) {
        ActivityCompat.requestPermissions(
                activity, requiredPermissions, code);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (grantResults.length > 0) {
                    boolean permissionGranted = true;
                    for (int i = 0; i < 4; ++i)
                        permissionGranted = permissionGranted &&
                                (grantResults[i] == PackageManager.PERMISSION_GRANTED);
                    if (permissionGranted)
                        onPermissionGranted();
                    else
                        Toast.makeText(getActivity(),
                                "Please grant all permissions!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("PERMISSIONS", "GrantResults length is zero!");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}