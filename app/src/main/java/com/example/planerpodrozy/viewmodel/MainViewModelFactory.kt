package com.example.planerpodrozy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.planerpodrozy.data.TravelDao

class MainViewModelFactory(
    private val dao: TravelDao
) : androidx.lifecycle.ViewModelProvider.Factory {

    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(dao) as T
    }
}