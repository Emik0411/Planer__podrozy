// "jedna podróż"
package com.example.planerpodrozy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "travel")
data class Travel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val location: String,
    val description: String,
    val startDate: String,
    val endDate: String,
    val lat: Double? = null,
    val lon: Double? = null
)