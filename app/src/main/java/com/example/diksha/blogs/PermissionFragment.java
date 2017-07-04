package com.example.diksha.blogs;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;


/**
 * Created by diksha on 3/7/17.
 */

public abstract class PermissionFragment extends Fragment {

    protected abstract void onPermissionGranted(final int ACTION_CODE);

    private static final String[] requiredPermissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    protected void doPermissionedAction(final int ACTION_CODE){
        if(isAllowedPermissions(getActivity()))
            onPermissionGranted(ACTION_CODE);
        else
            requestPermissions(getActivity(), ACTION_CODE);
    }

    private boolean isAllowedPermissions(Activity activity) {
        boolean permissionGranted = true;

        for (String permission : requiredPermissions)
            permissionGranted = permissionGranted &&
                    ContextCompat
                            .checkSelfPermission(activity, permission)
                            != PackageManager.PERMISSION_DENIED;

        return permissionGranted;
    }

    private void requestPermissions(Activity activity, final int ACTION_CODE) {
        ActivityCompat.requestPermissions(activity, requiredPermissions, ACTION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (grantResults.length > 0 && isAllowedPermissions(getActivity()))
            onPermissionGranted(requestCode);
        else {
            notifyUser(getView(), "App won't work without permission");
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    protected void notifyUser(View view, String message){
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
    }
}