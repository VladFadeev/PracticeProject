package com.example.practiceproject.data.places

import android.os.Parcel
import android.os.Parcelable

class Sight(
    name: String,
    latitude: Float,
    longitude: Float) : Place(name, latitude, longitude, PlaceType.Sight) {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readFloat()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeFloat(latitude)
        parcel.writeFloat(longitude)
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