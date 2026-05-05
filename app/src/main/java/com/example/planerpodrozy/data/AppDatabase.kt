// główna baza danych
package com.example.planerpodrozy.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Travel::class, Place::class],
    version = 7
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun travelDao(): TravelDao
    abstract fun placeDao(): PlaceDao
}