package com.example.planerpodrozy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planerpodrozy.api.RetrofitInstance
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.data.TravelDao
import com.example.planerpodrozy.model.Feature
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(
    private val dao: TravelDao
) : ViewModel() {

    // branie danych z bazy danych
    val travels: StateFlow<List<Travel>> =
        dao.getAllTravels()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _places = MutableStateFlow<List<Feature>>(emptyList())
    val places: StateFlow<List<Feature>> = _places

    // zapisywanie
    fun addTravel(name: String, location: String, description: String, start: String, end: String) {
        viewModelScope.launch {
            dao.insertTravel(
                Travel(
                    name = name,
                    location = location,
                    description = description,
                    startDate = start,
                    endDate = end
                )
            )
        }
    }

    // edytowanie
    fun updateTravel(travel: Travel) {
        viewModelScope.launch {
            dao.updateTravel(travel)
        }
    }

    fun searchPlaces(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _places.value = emptyList()
                return@launch
            }

            try {
                val response = RetrofitInstance.api.autocomplete(
                    text = query,
                    apiKey = "klucz",
                    lang = "pl"
                )
                _places.value = response.features
            } catch (e: Exception) {
                _places.value = emptyList()
            }
        }
    }

    // usuwanie
    fun deleteTravel(travel: Travel) {
        viewModelScope.launch {
            dao.deleteTravel(travel)
        }
    }
}