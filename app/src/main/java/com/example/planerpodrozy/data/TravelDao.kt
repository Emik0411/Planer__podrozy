package com.example.planerpodrozy.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TravelDao {

    @Query("SELECT * FROM travel ORDER BY id DESC")
    fun getAllTravels(): Flow<List<Travel>>

    @Insert
    suspend fun insertTravel(travel: Travel)

    @Delete
    suspend fun deleteTravel(travel: Travel)

    @Update
    suspend fun updateTravel(travel: Travel)
}