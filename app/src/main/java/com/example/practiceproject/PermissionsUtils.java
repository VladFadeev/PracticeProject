package com.example.practiceproject;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsUtils {

    private PermissionsUtils() { }

    private static final class InstanceHolder {
        static final PermissionsUtils INSTANCE = new PermissionsUtils();
    }

    public static PermissionsUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public boolean getLocationPermission(Activity activity) {
        boolean locationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
        }
        return locationPermissionGranted;
    }

    public boolean onRequestPermissionsResult(int requestCode,
                                           @NonNull int[] grantResults) {
        boolean permissionGranted = false;
        if (requestCode == 1) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true;
            }
        }
        return permissionGranted;
    }

    public boolean getInternetPermission(Activity activity) {
        boolean internetPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            internetPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.INTERNET}, 1);
        }
        return internetPermissionGranted;
    }
}
