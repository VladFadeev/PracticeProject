package com.example.practiceproject.map

import android.os.Bundle
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
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment

class MapFragment : Fragment(), MyUserInteractionListener {
    private var mapViewModel: MapViewModel? = null
    private var handler: SeekBarInactivityHandler? = null
    private val myLocationButtonMarginLeft = 25f
    private val myLocationButtonMarginBottom = 30f
    private var seekBarScale : Int? = null
    private var seekBarMax : Int? = null
    private var seekBarMin : Int? = null
    private var seekBarDefaultProgress : Int? = null
    private var delay : Long? = null
    private var startDelay : Long? = null
    private var seekBarStep : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        seekBarScale = resources.getInteger(R.integer.seekbar_scale)
        seekBarStep = seekBarScale!! / 2
        seekBarMax = resources.getInteger(R.integer.seekbar_max_progress)
        seekBarMin = resources.getInteger(R.integer.seekbar_min_progress)
        seekBarDefaultProgress = resources.getInteger(R.integer.seekbar_default_progress)
        delay = resources.getInteger(R.integer.interaction_end_delay).toLong()
        startDelay = resources.getInteger(R.integer.start_delay).toLong()
        val mapSupportFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (mapViewModel == null) {
            mapViewModel = ViewModelProvider(
                this,
                MapViewModelFactory(mapSupportFragment!!)
            )[MapViewModel::class.java]
            mapViewModel!!.map.observe(viewLifecycleOwner) { updateLocationUI() }
        }
        val seekbar = view.findViewById<SeekBar>(R.id.verticalSeekBar)
        seekbar.visibility = View.GONE
        seekbar.max = seekBarMax!! * seekBarScale!!
        seekbar.min = seekBarMin!! * seekBarScale!!
        seekbar.progress = seekBarDefaultProgress!! * seekBarScale!!
        seekbar.incrementProgressBy(seekBarStep!!)
        seekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                mapViewModel!!
                    .map.value!!.moveCamera(
                        CameraUpdateFactory
                            .zoomTo(
                                ((seekBarMax!! + seekBarMin!!) * seekBarScale!! - p1)
                                    .toFloat() / seekBarScale!!
                            )
                    )
                handler!!.stopHandler()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
                handler!!.stopHandler()
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
                handler!!.startHandler(delay!!)
            }
        })
        val button = view.findViewById<Button>(R.id.button)
        val customAnimations = CustomAnimations()
        handler = SeekBarInactivityHandler(button, seekbar, activity!!)
        button!!.setOnClickListener {
            customAnimations.expand(seekbar)
            customAnimations.collapse2d(button)
            handler!!.startHandler(startDelay!!)
            (activity as MainActivity).setInteractionListener(this)
        }
    }

    override fun onUserInteraction() {
        handler!!.resetHandler()
    }

    override fun onResume() {
        super.onResume()
        handler!!.resetHandler()
    }

    override fun onStop() {
        super.onStop()
        handler!!.stopHandler()
    }

    fun updateLocationUI() {
        val map = mapViewModel!!.map.value!!
        if (PermissionsUtils.isLocationPermissionGranted) {
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
            updateMapUI()
        } else {
            map.isMyLocationEnabled = false
            map.uiSettings.isMyLocationButtonEnabled = false
            PermissionsUtils.getLocationPermission(activity!!)
        }
    }

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
}