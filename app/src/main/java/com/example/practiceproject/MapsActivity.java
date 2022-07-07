package com.example.practiceproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final PermissionsUtils permissionsUtils = PermissionsUtils.getInstance();
    private static final RoutesUtil routesUtil = RoutesUtil.getInstance();
    private static final MetroService metroService = MetroService.getInstance();
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GoogleMap map;
    private Location lastKnownLocation;
    private Polyline polyline = null;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private static final LatLng defaultLocation = new LatLng(53.928566, 27.585802);
    private static final int CAMERA_ZOOM = 14;
    private static final String DRIVING = "driving";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior
                .from(findViewById(R.id.bottom_sheet));
        behavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        permissionsUtils.getLocationPermission(this);
        permissionsUtils.getInternetPermission(this);
        updateLocationUI();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit((Runnable) () -> metroService.addMetroMarkers(map));
        executorService.submit((Runnable) this::getDeviceLocation);
        executorService.shutdown();
        map.setOnMarkerClickListener(marker -> {
            BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior
                    .from(findViewById(R.id.bottom_sheet));
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            TextView name = findViewById(R.id.bottom_sheet_peek);
            name.setText(marker.getTitle());
            if (polyline != null) {
                polyline.remove();
            }
            polyline = map.addPolyline(routesUtil.drawRoute(
                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                    marker.getPosition(), DRIVING));
            return true;
        });
        map.setOnMapClickListener(latLng -> {
            polyline.remove();
            BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior
                    .from(findViewById(R.id.bottom_sheet));
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        });
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation() {
        try {
            if (PermissionsUtils.isLocationPermissionGranted()) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), CAMERA_ZOOM));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, CAMERA_ZOOM));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsUtils.onRequestPermissionsResult(requestCode, grantResults);
        switch (permissions[0]) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (!PermissionsUtils.isLocationPermissionGranted()) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                updateLocationUI();
                break;
            case Manifest.permission.INTERNET:
                if (!PermissionsUtils.isInternetPermissionGranted()) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (PermissionsUtils.isLocationPermissionGranted()) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                permissionsUtils.getLocationPermission(this);
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}