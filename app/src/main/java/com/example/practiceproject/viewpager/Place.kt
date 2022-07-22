package com.example.practiceproject.viewpager

import android.os.Parcelable

abstract class Place : Parcelable {
    protected abstract var name: String
    protected abstract var latitude: Float
    protected abstract var longitude: Float
    protected abstract var isFavorite: Boolean
}