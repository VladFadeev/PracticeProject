package com.example.practiceproject;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Used Bill Pugh implementation of the singleton pattern
 */
public class PermissionsUtils {
    private static boolean locationPermissionGranted;
    private static boolean internetPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_INTERNET = 2;

    private PermissionsUtils() {
    }

    private static final class InstanceHolder {
        static final PermissionsUtils INSTANCE = new PermissionsUtils();
    }

    public static PermissionsUtils getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static boolean isLocationPermissionGranted() {
        return locationPermissionGranted;
    }

    public static boolean isInternetPermissionGranted() {
        return internetPermissionGranted;
    }

    public void getLocationPermission(Activity activity) {
        locationPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void getInternetPermission(Activity activity) {
        internetPermissionGranted = false;
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(),
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            internetPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.INTERNET}, PERMISSIONS_REQUEST_INTERNET);
        }
    }

    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
                break;
            case PERMISSIONS_REQUEST_INTERNET:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    internetPermissionGranted = true;
                }
                break;
        }
    }
}
