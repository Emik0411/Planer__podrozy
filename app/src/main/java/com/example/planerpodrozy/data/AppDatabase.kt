// główna baza danych
package com.example.planerpodrozy.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Travel::class, Place::class, DiaryNote::class],
    version = 11
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun travelDao(): TravelDao
    abstract fun placeDao(): PlaceDao
    abstract fun diaryNoteDao(): DiaryNoteDao
}