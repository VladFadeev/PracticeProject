package com.example.practiceproject.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.SupportMapFragment

class MapViewModelFactory(private val supportMapFragment: SupportMapFragment) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapViewModel(supportMapFragment) as T
    }
}