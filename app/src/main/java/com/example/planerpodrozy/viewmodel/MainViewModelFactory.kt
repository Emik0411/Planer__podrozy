// tworzenie ViewModel
package com.example.planerpodrozy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.planerpodrozy.data.DiaryNoteDao
import com.example.planerpodrozy.data.PlaceDao
import com.example.planerpodrozy.data.TravelDao

// tworzy ViewModel i daje mu DAO
class MainViewModelFactory(
    private val travelDao: TravelDao,
    private val placeDao: PlaceDao,
    private val diaryNoteDao: DiaryNoteDao
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(travelDao, placeDao, diaryNoteDao) as T
    }
}