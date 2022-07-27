package com.example.practiceproject.data.places

import android.os.Parcel
import android.os.Parcelable

class Sight(name: String,
            latitude: Float,
            longitude: Float,
            isFavorite: Boolean = false) :
    Place(0, name, latitude, longitude, isFavorite, PlaceType.Sight) {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readBoolean()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(getName())
        parcel.writeFloat(getLatitude())
        parcel.writeFloat(getLongitude())
        parcel.writeBoolean(getIsFavorite())
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sight> {
        override fun createFromParcel(parcel: Parcel): Sight {
            return Sight(parcel)
        }

        override fun newArray(size: Int): Array<Sight?> {
            return arrayOfNulls(size)
        }
    }
}