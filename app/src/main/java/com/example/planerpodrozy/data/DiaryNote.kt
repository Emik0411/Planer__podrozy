package com.example.planerpodrozy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_note")
data class DiaryNote(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val travelId: Int,
    val date: String,
    val text: String,
    val createdAt: Long = System.currentTimeMillis()
)