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
        time: String,
        lat: Double,
        lon: Double
    ) {
        viewModelScope.launch {
            placeDao.insert(
                Place(
                    travelId = travelId,
                    date = date,
                    name = name,
                    category = category,
                    time = time,
                    lat = lat,
                    lon = lon
                )
            )
        }
    }

    fun getTravelById(id: Int): Flow<Travel?> {
        return dao.getTravelById(id)
    }

    fun getPlacesForDay(travelId: Int, date: String): Flow<List<Place>> {
        return placeDao.getPlacesForDay(travelId, date)
    }

    fun searchPlacesByCategory(
        category: String,
        query: String,
        location: String,
        onResult: (List<Feature>) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val mappedCategory = mapSubcategory(category)

                val response = RetrofitInstance.api.searchPlaces(
                    categories = mappedCategory,
                    filter = "circle:21.0122,52.2297,5000",
                    limit = 5,
                    apiKey = "klucz"
                )

                val filtered = response.features.filter {
                    it.geometry != null &&
                            (
                                    !it.properties.name.isNullOrBlank() ||
                                            !it.properties.formatted.isNullOrBlank()
                                    )
                }

                onResult(filtered)

            } catch (e: Exception) {
                onResult(emptyList())
            }
        }
    }
}





private fun mapSubcategory(sub: String): String {
    return when (sub) {

        "Restauracje" -> "catering.restaurant"
        "Kawiarnie" -> "catering.cafe"
        "Fast food" -> "catering.fast_food"

        "Muzea" -> "tourism.museum"
        "Zabytki" -> "tourism.sights"
        "Kościoły" -> "religion.place_of_worship"

        "Parki" -> "leisure.park"
        "Plaże" -> "natural.beach"
        "Góry" -> "natural.mountain"

        "Kina" -> "entertainment.cinema"
        "Kluby" -> "entertainment.nightclub"

        else -> "tourism"
    }
}