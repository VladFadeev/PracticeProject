package com.example.practiceproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StationsViewModel : ViewModel() {
    val stationsList = MutableLiveData<List<Station>>()

    init {
        StationsUtils.getStations(this, 5)
    }

    fun addStations() {
        StationsUtils.getStations(this)
    }
}