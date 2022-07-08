package com.example.practiceproject

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng as GmsLatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.model.LatLng as MapsLatLng
import java.time.Instant
import java.util.concurrent.Executors

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        val behavior = BottomSheetBehavior
            .from(findViewById<LinearLayout>(R.id.bottom_sheet))
        behavior.state = BottomSheetBehavior.STATE_HIDDEN

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        permissionsUtils.getLocationPermission(this)
        permissionsUtils.getInternetPermission(this)
        updateLocationUI()
        val executorService = Executors.newFixedThreadPool(2)
        executorService.submit { metroService.addMetroMarkers(map) }
        executorService.submit { deviceLocation }
        executorService.shutdown()
        map!!.setOnMarkerClickListener { marker: Marker ->
            val behavior = BottomSheetBehavior
                .from(findViewById<LinearLayout>(R.id.bottom_sheet))
            val name = findViewById<TextView>(R.id.bottom_sheet_peek)
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED && marker.title == name.text) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                polyline!!.remove()
            } else {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val content = findViewById<TextView>(R.id.bottom_sheet_content)
                val distanceMatrix = DistanceUtils.estimateRouteTime(
                    Instant.now(), MAPS_API_KEY,
                    false, null,
                    MapsLatLng(
                        lastKnownLocation!!.latitude,
                        lastKnownLocation!!.longitude
                    ),
                    MapsLatLng(
                        marker.position.latitude,
                        marker.position.longitude
                    )
                )
                val distance = distanceMatrix!!.rows[0].elements[0].distance
                name.text = marker.title
                if (polyline != null) {
                    polyline!!.remove()
                }
                polyline = map!!.addPolyline(
                    routesUtils.drawRoute(
                        GmsLatLng(lastKnownLocation!!.latitude, lastKnownLocation!!.longitude),
                        marker.position, MAPS_API_KEY
                    )!!
                )
                content.text = distance.humanReadable
            }
            true
        }
        map!!.setOnMapClickListener {
            polyline!!.remove()
            val behavior = BottomSheetBehavior
                .from(findViewById<LinearLayout>(R.id.bottom_sheet))
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }

    @get:SuppressLint("MissingPermission")
    private val deviceLocation: Unit
        get() {
            try {
                if (PermissionsUtils.isLocationPermissionGranted()) {
                    val locationResult = fusedLocationProviderClient!!.lastLocation
                    locationResult.addOnCompleteListener(this) { task: Task<Location?> ->
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            if (lastKnownLocation != null) {
                                map!!.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        GmsLatLng(
                                            lastKnownLocation!!.latitude,
                                            lastKnownLocation!!.longitude
                                        ), CAMERA_ZOOM.toFloat()
                                    )
                                )
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.")
                            Log.e(TAG, "Exception: %s", task.exception)
                            map!!.moveCamera(
                                CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, CAMERA_ZOOM.toFloat())
                            )
                            map!!.uiSettings.isMyLocationButtonEnabled = false
                        }
                    }
                }
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsUtils.onRequestPermissionsResult(requestCode, grantResults)
        when (permissions[0]) {
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                if (!PermissionsUtils.isLocationPermissionGranted()) {
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                }
                updateLocationUI()
            }
            Manifest.permission.INTERNET -> if (!PermissionsUtils.isInternetPermissionGranted()) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (PermissionsUtils.isLocationPermissionGranted()) {
                map!!.isMyLocationEnabled = true
                map!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map!!.isMyLocationEnabled = false
                map!!.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                permissionsUtils.getLocationPermission(this)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    companion object {
        private val permissionsUtils = PermissionsUtils.getInstance()
        private val routesUtils = RoutesUtils
        private val metroService = MetroService.getInstance()
        private val TAG: String = MapsActivity::class.toString()
        private val defaultLocation = GmsLatLng(53.928566, 27.585802)
        private const val CAMERA_ZOOM = 14
        private const val MAPS_API_KEY: String = BuildConfig.MAPS_API_KEY
    }
}