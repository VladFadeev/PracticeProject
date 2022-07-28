package com.example.practiceproject.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.core.app.ActivityCompat

/**
 * Used singleton pattern
 */
object PermissionsUtils {
    var isLocationPermissionGranted = false
        private set
    var isInternetPermissionGranted = false
        private set
    private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
    private const val PERMISSIONS_REQUEST_INTERNET = 2

    fun getLocationPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    fun getInternetPermission(activity: Activity) {
        if (ContextCompat.checkSelfPermission(
                activity.applicationContext,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            isInternetPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.INTERNET),
                PERMISSIONS_REQUEST_INTERNET
            )
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION ->                 // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    isLocationPermissionGranted = true
                }
            PERMISSIONS_REQUEST_INTERNET -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                isInternetPermissionGranted = true
            }
        }
    }
}