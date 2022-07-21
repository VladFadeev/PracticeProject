package com.example.practiceproject.viewpager

enum class Places(private val utils: PlacesUtils) {
    Stations(StationsUtils);//,
    //Sights(SightsUtils);

    fun getAll(model: PlacesViewModel): List<Place> = utils.getAll(model)

    fun addAll(model: PlacesViewModel): List<Place> = utils.addAll(model)
}