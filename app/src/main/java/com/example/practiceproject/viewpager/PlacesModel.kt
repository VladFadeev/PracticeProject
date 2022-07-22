package com.example.practiceproject.viewpager

import androidx.lifecycle.LiveData

object PlacesModel {
    fun getAllPlaces(data: LiveData<List<List<Place>>>){
        for (place in Places.values()){
            place.getAll(data)
        }
    }

    fun updateAllPlaces(data: LiveData<List<List<Place>>>) {
        for (place in Places.values()) {
            place.addAll(data)
        }
    }
}