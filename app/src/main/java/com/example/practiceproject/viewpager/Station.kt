package com.example.practiceproject.viewpager

data class Station(override var name: String,
                   override var latitude: Float,
                   override var longitude: Float) : Place() {
    val stationName: String
        get() = name
    val stationLatitude: Float
        get() = latitude
    val stationLongitude: Float
        get() = longitude
    override var isFavorite = false
}