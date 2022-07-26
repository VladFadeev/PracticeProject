package com.example.practiceproject.data.places

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
abstract class Place(var name: String,
                     var latitude: Float,
                     var longitude: Float,
                     private var type: PlaceType
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    fun getType() = type
    fun setType(value: PlaceType) {
        type = value
    }
    @ColumnInfo(name = "favorite")
    var isFavorite: Boolean = false
}