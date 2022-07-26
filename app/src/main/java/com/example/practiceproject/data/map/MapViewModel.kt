package com.example.practiceproject.data.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.practiceproject.ui.map.MapFragment
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment

class MapViewModel(supportMapFragment: SupportMapFragment) : ViewModel() {
    private val map = MutableLiveData<GoogleMap>()

    fun getMap() = map

    init {
        supportMapFragment.getMapAsync(supportMapFragment.parentFragment as MapFragment)
    }
}