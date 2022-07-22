package com.example.practiceproject.viewpager

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PlacesViewModel : ViewModel() {
    private val placesList: MutableLiveData<List<List<Place>>> by lazy {
        MutableLiveData<List<List<Place>>>().also {
           PlacesModel.getAllPlaces(it)
        }
    }

    fun getPlaces(): LiveData<List<List<Place>>> {
        return placesList
    }

    fun updateAllPlaces() {
        PlacesModel.updateAllPlaces(placesList)
    }
}