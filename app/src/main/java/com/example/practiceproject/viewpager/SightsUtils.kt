package com.example.practiceproject.viewpager

object SightsUtils: PlacesUtils {
    override fun getAll(model: PlacesViewModel): List<Place> {
        TODO("JSON and DB access")
    }

    override fun addAll(model: PlacesViewModel): List<Place> {
        return getAll(model)
    }
}