package com.example.practiceproject.viewpager

data class Sight(override var name: String,
                 override var latitude: Float,
                 override var longitude: Float,
                 override var isFavorite: Boolean = false) : Place() {

}