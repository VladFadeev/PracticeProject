package com.example.practiceproject.viewpager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.practiceproject.viewpager.Station
import com.example.practiceproject.viewpager.StationsUtils

class StationsViewModel : ViewModel() {
    val stationsList = MutableLiveData<List<Station>>()

    init {
        StationsUtils.getStations(this, 5)
    }

    fun updateStations() {
        StationsUtils.getStations(this)
    }
}