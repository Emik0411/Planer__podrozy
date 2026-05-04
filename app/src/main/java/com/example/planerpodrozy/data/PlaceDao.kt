package com.example.planerpodrozy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaceDao {

    @Query("SELECT * FROM place WHERE travelId = :travelId AND date = :date")
    fun getPlacesForDay(travelId: Int, date: String): Flow<List<Place>>

    @Insert
    suspend fun insertPlace(place: Place)
}