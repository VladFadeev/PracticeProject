package com.example.practiceproject

import android.app.Application
import androidx.room.Room
import com.example.practiceproject.data.db.AppDatabase
import com.example.practiceproject.data.db.PlaceDao

val dao: PlaceDao by lazy {
    App.dao
}

class App: Application() {

    companion object {
        lateinit var dao: PlaceDao
    }

    override fun onCreate() {
        super.onCreate()
        dao = Room.databaseBuilder(applicationContext,
            AppDatabase::class.java, "database")
            .build().placeDao()
    }
}