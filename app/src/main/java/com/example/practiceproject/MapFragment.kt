package com.example.practiceproject

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import java.util.concurrent.Executors


class MapFragment : Fragment(), OnMapReadyCallback {
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var map: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private var polyline: Polyline? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        if (map == null) {
            val mapFragment = childFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }
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

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        PermissionsUtils.getLocationPermission(activity!!)
        PermissionsUtils.getInternetPermission(activity!!)
        updateLocationUI()
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit { deviceLocation }
        executorService.shutdown()
    }

    private val deviceLocation: Unit
        get() {
            try {
                if (PermissionsUtils.isLocationPermissionGranted) {
                    val locationResult = fusedLocationProviderClient!!.lastLocation
                    locationResult.addOnCompleteListener(activity!!) { task: Task<Location?> ->
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            if (lastKnownLocation != null) {
                                map!!.moveCamera(
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
                            map!!.moveCamera(
                                CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_CAMERA_ZOOM.toFloat())
                            )
                            map!!.uiSettings.isMyLocationButtonEnabled = false
                        }
                    }
                }
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }

    @SuppressLint("MissingPermission")
    fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (PermissionsUtils.isLocationPermissionGranted) {
                map!!.isMyLocationEnabled = true
                map!!.uiSettings.isMyLocationButtonEnabled = true
                val mapLocation =  view!!.findViewById<View>(R.id.map)
                    .findViewWithTag<View>("GoogleMapMyLocationButton")
                (mapLocation.parent as ViewGroup).post {
                    // create layoutParams, giving it our wanted width and height(important, by default the width is "match parent")
                    val rlp = RelativeLayout.LayoutParams(mapLocation.width, mapLocation.height)
                    val marginPixels = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        25f, resources.displayMetrics).toInt()
                    // position on top right
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
                    rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                    //give compass margin
                    rlp.setMargins(marginPixels,0,0,marginPixels)
                    mapLocation.layoutParams = rlp
                }
            } else {
                map!!.isMyLocationEnabled = false
                map!!.uiSettings.isMyLocationButtonEnabled = false
                lastKnownLocation = null
                PermissionsUtils.getLocationPermission(activity!!)
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message!!)
        }
    }

    companion object {
        private const val CAMERA_ZOOM = 14
        private const val DEFAULT_CAMERA_ZOOM = 12
        private const val MAPS_API_KEY: String = BuildConfig.MAPS_API_KEY
        private val TAG: String = Fragment::class.toString()
        private val defaultLocation = LatLng(53.9193, 27.5768)
    }
}