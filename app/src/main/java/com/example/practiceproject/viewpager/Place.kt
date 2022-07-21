package com.example.practiceproject.viewpager

abstract class Place {
    protected abstract var name: String
    protected abstract var latitude: Float
    protected abstract var longitude: Float
    protected abstract var isFavorite: Boolean
}