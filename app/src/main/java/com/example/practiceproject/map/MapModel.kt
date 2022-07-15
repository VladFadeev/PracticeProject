package com.example.practiceproject.map

import android.location.Location
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.practiceproject.BuildConfig
import com.example.practiceproject.PermissionsUtils
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.tasks.Task
import java.util.concurrent.Executors

class MapModel(private val supportMapFragment: SupportMapFragment,
               private val model: MapViewModel) : OnMapReadyCallback {
    private val fusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(supportMapFragment.activity!!)
    private var lastKnownLocation : Location? = null
    //private var polyline: Polyline? = null

    override fun onMapReady(googleMap: GoogleMap) {
        model.map.value = googleMap
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit { deviceLocation }
        executorService.shutdown()
    }

    private val deviceLocation: Unit
        get() {
            try {
                if (PermissionsUtils.isLocationPermissionGranted) {
                    val locationResult = fusedLocationProviderClient.lastLocation
                    locationResult.addOnCompleteListener(supportMapFragment.activity!!)
                    { task: Task<Location?> ->
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            if (lastKnownLocation != null) {
                                model.map.value!!.moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(
                                            lastKnownLocation!!.latitude,
                                            lastKnownLocation!!.longitude
                                        ), CAMERA_ZOOM.toFloat()
                                    )
                                )
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.")
                            Log.e(TAG, "Exception: %s", task.exception)
                            model.map.value!!.moveCamera(
                                CameraUpdateFactory
                                    .newLatLngZoom(
                                        defaultLocation,
                                        DEFAULT_CAMERA_ZOOM.toFloat()
                                    )
                            )
                            model.map.value!!.uiSettings.isMyLocationButtonEnabled = false
                        }
                    }
                }
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }

    companion object {
        private const val CAMERA_ZOOM = 14
        private const val DEFAULT_CAMERA_ZOOM = 12
        private const val MAPS_API_KEY: String = BuildConfig.MAPS_API_KEY
        private val TAG: String = Fragment::class.toString()
        private val defaultLocation = LatLng(53.9193, 27.5768)
    }

    /*override fun onMapReady(googleMap: GoogleMap) {
        val behavior = BottomSheetBehavior
            .from(view!!.findViewById<LinearLayout>(R.id.bottom_sheet))
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        map = googleMap
        PermissionsUtils.getLocationPermission(activity!!)
        PermissionsUtils.getInternetPermission(activity!!)
        updateLocationUI()
        val executorService = Executors.newFixedThreadPool(2)
        executorService.submit { MetroService.addMetroMarkers(map!!) }
        executorService.submit { deviceLocation }
        executorService.shutdown()
        map!!.setOnMarkerClickListener { marker: Marker ->
            val name = view!!.findViewById<TextView>(R.id.bottom_sheet_peek)
            if (behavior.state == BottomSheetBehavior.STATE_COLLAPSED && marker.title == name.text) {
                behavior.state = BottomSheetBehavior.STATE_HIDDEN
                polyline!!.remove()
            } else {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
                val content = view!!.findViewById<TextView>(R.id.bottom_sheet_content)
                val distanceMatrix = DistanceUtils.estimateRouteTime(
                    Instant.now(), MAPS_API_KEY,
                    false, null,
                    LatLng(
                        lastKnownLocation!!.latitude,
                        lastKnownLocation!!.longitude
                    ),
                    LatLng(
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
                    RoutesUtils.drawRoute(
                        com.google.android.gms.maps.model.LatLng(
                            lastKnownLocation!!.latitude,
                            lastKnownLocation!!.longitude
                        ),
                        marker.position, MAPS_API_KEY
                    )!!
                )
                content.text = distance.humanReadable
            }
            true
        }
        map!!.setOnMapClickListener {
            polyline!!.remove()
            behavior.setState(BottomSheetBehavior.STATE_HIDDEN)
        }
    }*/

}