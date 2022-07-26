package com.example.practiceproject.data.places

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PlacesViewModel : ViewModel() {
    private val placesList: MutableLiveData<List<List<Place>>> by lazy {
        MutableLiveData<List<List<Place>>>().also {
            it.value = PlacesModel.getAllPlaces()
        }
    }

    fun getPlaces() = placesList

    fun updateAllPlaces() {
        PlacesModel.updateAllPlaces(placesList)
    }
}