package com.example.planerpodrozy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "place")
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val travelId: Int,
    val date: String,

    val name: String,
    val category: String,
    val time: String,

    val description: String = "",

    val lat: Double? = null,
    val lon: Double? = null
)