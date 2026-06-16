package com.example.planerpodrozy.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryNoteDao {

    @Query("""
        SELECT * FROM diary_note 
        WHERE travelId = :travelId AND date = :date 
        ORDER BY createdAt DESC
    """)
    fun getNotes(travelId: Int, date: String): Flow<List<DiaryNote>>

    @Insert
    suspend fun insert(note: DiaryNote)

    @Delete
    suspend fun delete(note: DiaryNote)
}