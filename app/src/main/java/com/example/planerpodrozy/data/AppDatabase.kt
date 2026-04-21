package com.example.planerpodrozy.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Travel::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun travelDao(): TravelDao
}