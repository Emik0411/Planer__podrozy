// "mózg aplikacji"
package com.example.planerpodrozy.viewmodel

import android.util.Log
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
    fun addTravel(name: String, location: String, description: String, start: String, end: String, lat: Double?, lon: Double?) {
        viewModelScope.launch {
            dao.insertTravel(
                Travel(
                    name = name,
                    location = location,
                    description = description,
                    startDate = start,
                    endDate = end,
                    lat = lat,
                    lon = lon
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
        description: String,
        lat: Double?,
        lon: Double?
    ) {
        viewModelScope.launch {
            placeDao.insert(
                Place(
                    travelId = travelId,
                    date = date,
                    name = name,
                    category = category,
                    time = time,
                    description = description,
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

    fun getLatLonFromAddress(
        address: String,
        onResult: (Double?, Double?) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.geocodeAddress(
                    text = address,
                    apiKey = "klucz"
                )

                val feature = response.features.firstOrNull()

                val coords = feature?.geometry?.coordinates
                val lon = coords?.getOrNull(0)
                val lat = coords?.getOrNull(1)

                onResult(lat, lon)

            } catch (e: Exception) {
                onResult(null, null)
            }
        }
    }

    fun searchPlacesByCategory(
        category: String,
        query: String,
        travel: Travel,
        onResult: (List<Feature>) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val mappedCategory = mapSubcategory(category)

                val response = RetrofitInstance.api.searchPlaces(
                    categories = mappedCategory,
                    filter = "circle:${travel.lon},${travel.lat},20000",
                    limit = 50,
                    apiKey = "klucz"
                )

                val filtered = response.features
                    .filter { it.geometry != null }
                    .filter { feature ->

                        val coords = feature.geometry?.coordinates ?: return@filter false
                        val lon = coords.getOrNull(0) ?: return@filter false
                        val lat = coords.getOrNull(1) ?: return@filter false

                        val travelLat = travel.lat
                        val travelLon = travel.lon

                        if (travelLat == null || travelLon == null) {
                            return@filter false
                        }

                        val distance = distanceKm(
                            travelLat,
                            travelLon,
                            lat,
                            lon
                        )

                        distance <= 20.0 && (
                                feature.properties.name?.contains(query, true) == true ||
                                        feature.properties.formatted?.contains(query, true) == true
                                )
                    }
                    .take(5)

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

        "Muzea" -> "entertainment.museum"
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

private fun distanceKm(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val R = 6371.0

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a =
        Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) *
                Math.sin(dLon / 2)

    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

    return R * c
}