// "mózg aplikacji"
package com.example.planerpodrozy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.planerpodrozy.api.RetrofitInstance
import com.example.planerpodrozy.data.Place
import com.example.planerpodrozy.data.PlaceDao
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.data.TravelDao
import com.example.planerpodrozy.model.Feature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel(
    private val dao: TravelDao,
    private val placeDao: PlaceDao

) : ViewModel() {

    // branie danych z bazy danych
    val travels: StateFlow<List<Travel>> =
        dao.getAllTravels()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // lista miejsc z API
    private val _places = MutableStateFlow<List<Feature>>(emptyList()) // dla ViewModel
    val places: StateFlow<List<Feature>> = _places // dla UI

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

    // wyszukiwanie miejsc
    fun searchPlaces(query: String) {
        viewModelScope.launch {
            // sprawdzenie czy jest coś wpisane
            if (query.isBlank()) {
                _places.value = emptyList()
                return@launch
            }

            try {
                // zapytanie do API
                val response = RetrofitInstance.api.autocomplete(
                    text = query,
                    apiKey = "klucz",
                    lang = "pl"
                )
                // odpowiedź API
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


    fun addPlace(
        travelId: Int,
        date: String,
        name: String,
        category: String,
        time: String
    ) {
        viewModelScope.launch {
            placeDao.insertPlace(
                Place(
                    travelId = travelId,
                    date = date,
                    name = name,
                    category = category,
                    time = time
                )
            )
        }
    }

    fun getPlacesForDay(travelId: Int, date: String): Flow<List<Place>> {
        return placeDao.getPlacesForDay(travelId, date)
    }


}