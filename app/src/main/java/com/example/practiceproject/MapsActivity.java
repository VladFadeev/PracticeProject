package com.example.practiceproject;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationProviderClient;
    private final PermissionsUtils permissionsUtils = PermissionsUtils.getInstance();
    private boolean locationPermissionGranted;
    private boolean internetPermissionGranted;
    private Location lastKnownLocation;
    private GoogleMap map;
    private static final String TAG = MapsActivity.class.getSimpleName();
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        locationPermissionGranted = permissionsUtils.getLocationPermission(this);
        internetPermissionGranted = permissionsUtils.getInternetPermission(this);
        updateLocationUI();
        addMetroMarkers();
        getDeviceLocation();
    }

    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                @SuppressLint("MissingPermission") Task<Location> locationResult =
                        fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        lastKnownLocation = task.getResult();
                        if (lastKnownLocation != null) {
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(),
                                            lastKnownLocation.getLongitude()), 14));
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        map.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(defaultLocation, 15));
                        map.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (permissions[0]) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (!(locationPermissionGranted = permissionsUtils.onRequestPermissionsResult(requestCode, grantResults))) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                updateLocationUI();
                break;
            case Manifest.permission.INTERNET:
                if(!(internetPermissionGranted = permissionsUtils.onRequestPermissionsResult(requestCode, grantResults))) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
        }
    }

    private void addMetroMarkers() {
        if (internetPermissionGranted) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://my-json-server.typicode.com/BeeWhy/metro/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            MetroAPI metroAPI = retrofit.create(MetroAPI.class);
            Call<List<Station>> stations = metroAPI.stations();
            stations.enqueue(new Callback<List<Station>>() {
                @Override
                public void onResponse(@NonNull Call<List<Station>> call,
                                       @NonNull Response<List<Station>> response) {

                    for (Station s : response.body()) {
                        if (!Objects.equals(s.getName().toLowerCase(), "error")) {
                            map.addMarker(new MarkerOptions()
                                    .position(new LatLng(s.getLatitude(), s.getLongitude()))
                                    .title(s.getName()));
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<Station>> call,
                                      @NonNull Throwable t) {
                    Log.d("debug","response failed");
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                locationPermissionGranted = permissionsUtils.getLocationPermission(this);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }
}