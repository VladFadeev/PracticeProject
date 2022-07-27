package com.example.practiceproject.data.places

import android.os.Parcel
import android.os.Parcelable

class Station(name: String,
              latitude: Float,
              longitude: Float) :
    Place(0, name, latitude, longitude, false, PlaceType.Station) {

    public override fun setType(value: PlaceType) {
        super.setType(value)
    }

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(getName())
        parcel.writeFloat(getLatitude())
        parcel.writeFloat(getLongitude())
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