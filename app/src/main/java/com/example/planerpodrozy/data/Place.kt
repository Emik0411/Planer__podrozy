package com.example.planerpodrozy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Place(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val travelId: Int,
    val category: String,
    val date: String,
    val name: String,
    val time: String
)