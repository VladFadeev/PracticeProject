package com.example.practiceproject.viewpager

import android.os.Parcel
import android.os.Parcelable

data class Sight(override var name: String,
                 override var latitude: Float,
                 override var longitude: Float,
                 override var isFavorite: Boolean = false) : Place() {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readByte() != 0.toByte()
    ) {
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

    companion object CREATOR : Parcelable.Creator<Sight> {
        override fun createFromParcel(parcel: Parcel): Sight {
            return Sight(parcel)
        }

        override fun newArray(size: Int): Array<Sight?> {
            return arrayOfNulls(size)
        }
    }

}