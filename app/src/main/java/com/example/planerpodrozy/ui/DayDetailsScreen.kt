package com.example.planerpodrozy.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.viewmodel.MainViewModel
import android.view.ViewGroup
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.MapView
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

@Composable
fun DayDetailsScreen(
    travel: Travel,
    date: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onAddPlace: () -> Unit
) {

    val places by viewModel
        .getPlacesForDay(travel.id, date)
        .collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {



        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            factory = { context ->

                val mapView = MapView(context)

                mapView.setMultiTouchControls(true)

                val controller = mapView.controller
                controller.setZoom(10.0)

                val startPoint = if (travel.lat != null && travel.lon != null) {
                    GeoPoint(travel.lat, travel.lon)
                } else {
                    GeoPoint(52.2297, 21.0122)
                }
                controller.setCenter(startPoint)

                mapView
            },
            update = { mapView ->

                // czyszczenie
                mapView.overlays.clear()

                // rysowanie każdej pinezki
                places.forEach { place ->

                    val lat = place.lat
                    val lon = place.lon

                    // jeśli brak współrzędnych, nie pokazujemy na mapie
                    if (lat == null || lon == null) {
                        return@forEach
                    }

                    val point = GeoPoint(lat, lon)

                    val marker = Marker(mapView)
                    marker.position = point
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = place.name

                    mapView.overlays.add(marker)
                }

                val firstWithCoordinates = places.firstOrNull {
                    it.lat != null && it.lon != null
                }

                if (firstWithCoordinates != null) {
                    val controller = mapView.controller
                    controller.setZoom(12.0)
                    controller.setCenter(
                        GeoPoint(
                            firstWithCoordinates.lat!!,
                            firstWithCoordinates.lon!!
                        )
                    )
                }

                mapView.invalidate()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAddPlace,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Dodaj atrakcję")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onBack,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Wstecz")
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (places.isEmpty()) {
            Text(
                text = "Brak atrakcji tego dnia",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(places) { place ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "${place.time}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (place.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = place.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Text(
                            text = place.category,
                            style = MaterialTheme.typography.bodySmall
                        )


                    }

                    Divider()
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


    }
}