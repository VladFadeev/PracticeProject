package com.example.practiceproject.data.places

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
abstract class Place(@PrimaryKey(autoGenerate = true) private var id: Int,
                     private var name: String,
                     private var latitude: Float,
                     private var longitude: Float,
                     @ColumnInfo(name = "favorite")
                     private var isFavorite: Boolean,
                     private var type: PlaceType,
                     ) : Parcelable {
    fun getId() = id
    fun getName() = name
    fun getLatitude() = latitude
    fun getLongitude() = longitude
    fun getIsFavorite() = isFavorite
    fun getType() = type
    protected open fun setType(value: PlaceType) {
        type = value
    }
}