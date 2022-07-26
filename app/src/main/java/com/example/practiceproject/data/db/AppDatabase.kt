package com.example.practiceproject.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practiceproject.data.places.Place

@Database(entities = [Place::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun placeDao(): PlaceDao
}