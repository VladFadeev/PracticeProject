package com.example.practiceproject.ui.map

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.practiceproject.*
import com.example.practiceproject.data.map.MapViewModel
import com.example.practiceproject.data.map.MapViewModelFactory
import com.example.practiceproject.ui.MainActivity
import com.example.practiceproject.utils.CustomAnimations
import com.example.practiceproject.utils.MyUserInteractionListener
import com.example.practiceproject.utils.PermissionsUtils
import com.example.practiceproject.utils.SeekBarInactivityHandler
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import java.util.concurrent.Executors

class MapFragment : Fragment(), MyUserInteractionListener, OnMapReadyCallback {
    private var mapViewModel: MapViewModel? = null
    private var handler: SeekBarInactivityHandler? = null
    private val myLocationButtonMarginLeft = 25f
    private val myLocationButtonMarginBottom = 30f
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation : Location? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val seekBarScale = resources.getInteger(R.integer.seekbar_scale)
        val seekBarStep = seekBarScale / 2
        val seekBarMax = resources.getInteger(R.integer.seekbar_max_progress)
        val seekBarMin = resources.getInteger(R.integer.seekbar_min_progress)
        val seekBarDefaultProgress = resources.getInteger(R.integer.seekbar_default_progress)
        val delay = resources.getInteger(R.integer.interaction_end_delay).toLong()
        val startDelay = resources.getInteger(R.integer.start_delay).toLong()
        val mapSupportFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity!!)
        if (mapViewModel == null) {
            mapViewModel = ViewModelProvider(
                this,
                MapViewModelFactory(mapSupportFragment!!)
            )[MapViewModel::class.java]
            mapViewModel!!.getMap().observe(viewLifecycleOwner) { updateLocationUI() }
        }
        val seekbar = view.findViewById<SeekBar>(R.id.verticalSeekBar)
        seekbar.visibility = View.GONE
        seekbar.max = seekBarMax * seekBarScale
        seekbar.min = seekBarMin * seekBarScale
        seekbar.progress = seekBarDefaultProgress * seekBarScale
        seekbar.incrementProgressBy(seekBarStep)
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mapViewModel!!
                    .getMap().value!!.moveCamera(
                        CameraUpdateFactory
                            .zoomTo(
                                ((seekBarMax + seekBarMin) * seekBarScale - p1)
                                    .toFloat() / seekBarScale
                            )
                    )
                handler!!.stopHandler()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                handler!!.stopHandler()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                handler!!.startHandler(delay)
            }
        })
        val button = view.findViewById<Button>(R.id.button)
        handler = SeekBarInactivityHandler(button, seekbar, activity!!)
        button!!.setOnClickListener {
            CustomAnimations.expand(seekbar)
            CustomAnimations.collapse2d(button)
            handler!!.startHandler(startDelay)
            (activity as MainActivity).setInteractionListener(this)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mapViewModel!!.getMap().value = p0
        defaultLocation()
        updateDeviceLocation()
    }

    fun updateDeviceLocation() {
        updateLocationUI()
        val executorService = Executors.newSingleThreadExecutor()
        executorService.submit { deviceLocation }
        executorService.shutdown()
    }

    private val deviceLocation: Unit
        get() {
            try {
                if (PermissionsUtils.isLocationPermissionGranted) {
                    val locationResult = fusedLocationProviderClient.lastLocation
                    locationResult.addOnCompleteListener(activity!!)
                    { task: Task<Location?> ->
                        if (task.isSuccessful) {
                            lastKnownLocation = task.result
                            if (lastKnownLocation != null) {
                                mapViewModel!!.getMap().value!!.moveCamera(
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
                            defaultLocation()
                        }
                    }
                }
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message, e)
            }
        }

    private fun defaultLocation() {
        mapViewModel!!.getMap().value!!.moveCamera(
            CameraUpdateFactory
                .newLatLngZoom(
                    defaultLocation,
                    DEFAULT_CAMERA_ZOOM.toFloat()
                )
        )
        mapViewModel!!.getMap().value!!.uiSettings.isMyLocationButtonEnabled = false
    }

    fun updateLocationUI() {
        val map = mapViewModel!!.getMap().value!!
        if (PermissionsUtils.isLocationPermissionGranted) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            updateMapUI()
        } else {
            map.isMyLocationEnabled = false
            map.uiSettings.isMyLocationButtonEnabled = false
        }
    }

    override fun onUserInteraction() = handler!!.resetHandler()

    override fun onResume() {
        super.onResume()
        handler!!.resetHandler()
    }

    override fun onStop() {
        super.onStop()
        handler!!.stopHandler()
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

    private fun updateMapUI() {
        val mapLocation = view!!.findViewById<View>(R.id.map)
            .findViewWithTag<View>("GoogleMapMyLocationButton")
        (mapLocation.parent as ViewGroup).post {
            // create layoutParams, giving it our wanted width and height
            // (important, by default the width is "match parent")
            val rlp = RelativeLayout.LayoutParams(mapLocation.width, mapLocation.height)
            val marginLeft = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                myLocationButtonMarginLeft, resources.displayMetrics
            ).toInt()
            val marginBottom = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                myLocationButtonMarginBottom, resources.displayMetrics
            ).toInt()
            // position on bottom left
            rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            //give compass margin
            rlp.setMargins(marginLeft, 0, 0, marginBottom)
            mapLocation.layoutParams = rlp
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