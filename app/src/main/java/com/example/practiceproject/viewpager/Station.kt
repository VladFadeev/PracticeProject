package com.example.practiceproject.viewpager

import java.lang.StringBuilder
import java.util.*

class Station(val name: String, val latitude: Float, val longitude: Float) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val station = other as Station
        return station.latitude.compareTo(latitude) == 0 &&
                station.longitude.compareTo(longitude) == 0 &&
                name == station.name
    }

    override fun hashCode(): Int {
        return Objects.hash(name, latitude, longitude)
    }

    override fun toString(): String {
        val sb = StringBuilder("Station{")
        sb.append("name='").append(name).append('\'')
        sb.append(", latitude=").append(latitude)
        sb.append(", longitude=").append(longitude)
        sb.append('}')
        return sb.toString()
    }
}