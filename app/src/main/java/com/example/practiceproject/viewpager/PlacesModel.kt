package com.example.practiceproject.viewpager

object PlacesModel {
    fun getAllPlaces(model: PlacesViewModel){
        for (place in Places.values()){
            place.getAll(model)
        }
    }

    fun updateAllPlaces(model: PlacesViewModel) {
        for (place in Places.values()) {
            (model.placesList.value!! as MutableList).add(place.ordinal,place.addAll(model))
        }
    }
}