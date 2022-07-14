package com.example.practiceproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MetroStations : Fragment() {
    private val model : StationsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null && model.stationsList.value!!.size < 7) {
            model.addStations()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_metro_stations, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val behavior = BottomSheetBehavior
            .from(view.findViewById<LinearLayout>(R.id.bottom_sheet))
        behavior.state = BottomSheetBehavior.STATE_HIDDEN
        val layout = view.findViewById<LinearLayout>(R.id.stationsList)
        val stationsObserver = Observer<List<Station>> {stationList ->
            StationsUtils.addStationsList(layout, view.findViewById(R.id.bottom_sheet), stationList)
        }
        model.stationsList.observe(viewLifecycleOwner, stationsObserver)
    }
}