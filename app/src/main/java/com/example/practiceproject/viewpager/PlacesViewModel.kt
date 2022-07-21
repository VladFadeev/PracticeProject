package com.example.practiceproject.viewpager

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlacesViewModel : ViewModel() {
    val placesList = MutableLiveData<List<List<Place>>>()

    init {
        placesList.value = ArrayList()
        PlacesModel.getAllPlaces(this)
    }

    fun updateAllPlaces() {
        PlacesModel.updateAllPlaces(this)
    }
}