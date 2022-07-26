package com.example.practiceproject.data.db

import android.database.Cursor
import androidx.room.*
import com.example.practiceproject.data.places.Place
import com.example.practiceproject.data.places.PlaceType
import com.example.practiceproject.data.places.Sight
import com.example.practiceproject.data.places.Station


@Dao
abstract class PlaceDao {
    @Insert
    abstract fun insertAll(vararg places: Place)

    @Insert
    abstract fun insertAll(list: List<Place>)

    @Update
    abstract fun updatePlaces(vararg places: Place)

    @Delete
    abstract fun delete(place: Place)

    @Query("SELECT * FROM place")
    protected abstract fun getAllInternal(): Cursor

    @Query("SELECT * FROM place WHERE type = :type")
    protected abstract fun getAllByTypeInternal(type: PlaceType): Cursor

    fun getAll(): List<Place> {
        var cursor: Cursor? = null
        return try {
            val placeList: MutableList<Place> = ArrayList()
            cursor = getAllInternal()
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    // use the discriminator value here
                    val placeType =
                        cursor.getInt(cursor.getColumnIndex("type"))
                    var place: Place? = null
                    if (placeType == PlaceType.Station.ordinal) {
                        place = Station(cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getFloat(cursor.getColumnIndex("latitude")),
                            cursor.getFloat(cursor.getColumnIndex("longitude")))
                    } else if (placeType == PlaceType.Sight.ordinal) {
                        place =
                            Sight(cursor.getString(cursor.getColumnIndex("name")),
                                cursor.getFloat(cursor.getColumnIndex("latitude")),
                                cursor.getFloat(cursor.getColumnIndex("longitude")))
                    }
                    placeList.add(place!!)
                } while (cursor.moveToNext())
            }
            placeList
        } finally {
            cursor?.close()
        }
    }

    fun getAllStations(): List<Station> {
        var cursor: Cursor? = null
        return try {
            val stationList: MutableList<Station> = ArrayList()
            cursor = getAllByTypeInternal(PlaceType.Station)
            if (cursor.moveToFirst()) {
                do {
                    val station = Station(cursor.getString(cursor.getColumnIndex("name")),
                            cursor.getFloat(cursor.getColumnIndex("latitude")),
                            cursor.getFloat(cursor.getColumnIndex("longitude")))
                    stationList.add(station)
                } while (cursor.moveToNext())
            }
            stationList
        } finally {
            cursor?.close()
        }
    }

    fun getAllSights(): List<Sight> {
        var cursor: Cursor? = null
        return try {
            val sightList: MutableList<Sight> = ArrayList()
            cursor = getAllByTypeInternal(PlaceType.Sight)
            if (cursor.moveToFirst()) {
                do {
                    val sight = Sight(cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getFloat(cursor.getColumnIndex("latitude")),
                        cursor.getFloat(cursor.getColumnIndex("longitude")))
                    sightList.add(sight)
                } while (cursor.moveToNext())
            }
            sightList
        } finally {
            cursor?.close()
        }
    }
}