package com.example.practiceproject.viewpager

import android.os.Parcel
import android.os.Parcelable

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

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readFloat()
    ) {
        isFavorite = parcel.readByte() != 0.toByte()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeFloat(latitude)
        parcel.writeFloat(longitude)
        parcel.writeByte(if (isFavorite) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Station> {
        override fun createFromParcel(parcel: Parcel): Station {
            return Station(parcel)
        }

        override fun newArray(size: Int): Array<Station?> {
            return arrayOfNulls(size)
        }
    }
}