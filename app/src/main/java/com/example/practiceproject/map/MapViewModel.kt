package com.example.practiceproject.map

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment

class MapViewModel(supportMapFragment: SupportMapFragment) : ViewModel() {
    val map = MutableLiveData<GoogleMap>()

    init {
        supportMapFragment.getMapAsync(MapModel(supportMapFragment, this))
    }
}