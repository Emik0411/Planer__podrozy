package com.example.planerpodrozy.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.viewmodel.MainViewModel
import androidx.compose.ui.viewinterop.AndroidView
import com.example.planerpodrozy.data.Place

import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.maps.MapLibreMap

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

    val notes by viewModel
        .getDiaryNotes(travel.id, date)
        .collectAsState(initial = emptyList())

    var newNote by remember { mutableStateOf("") }

    var showMap by remember { mutableStateOf(false) }

    var mapRef by remember { mutableStateOf<MapLibreMap?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { showMap = false }) {
                Text("Lista")
            }

            Button(onClick = { showMap = true }) {
                Text("Mapa")
            }
        }

        if (showMap) {

            Box(modifier = Modifier.fillMaxSize()) {

                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->

                        val mapView = org.maplibre.android.maps.MapView(context)

                        mapView.getMapAsync { map ->

                            mapRef = map

                            map.setStyle(
                                "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"
                            ) {

                                map.clear()

                                val points = mutableListOf<LatLng>()

                                val startPoint = if (travel.lat != null && travel.lon != null) {
                                    LatLng(travel.lat, travel.lon)
                                } else {
                                    LatLng(52.2297, 21.0122)
                                }

                                map.cameraPosition = CameraPosition.Builder()
                                    .target(startPoint)
                                    .zoom(11.5)
                                    .build()

                                places.forEach { place ->
                                    val lat = place.lat ?: return@forEach
                                    val lon = place.lon ?: return@forEach

                                    val point = LatLng(lat, lon)
                                    points.add(point)

                                    map.addMarker(
                                        MarkerOptions()
                                            .position(point)
                                            .title(place.name)
                                    )
                                }

                                if (points.size > 1) {
                                    map.addPolyline(
                                        org.maplibre.android.annotations.PolylineOptions()
                                            .addAll(points)
                                            .color(android.graphics.Color.BLUE)
                                            .width(5f)
                                    )
                                }

                                points.firstOrNull()?.let {
                                    map.animateCamera(
                                        CameraUpdateFactory.newLatLngZoom(it, 12.5)
                                    )
                                }
                            }
                        }

                        mapView
                    }
                )

                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .align(androidx.compose.ui.Alignment.TopEnd)
                ) {
                    Button(onClick = {
                        mapRef?.animateCamera(CameraUpdateFactory.zoomIn())
                    }) {
                        Text("+")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(onClick = {
                        mapRef?.animateCamera(CameraUpdateFactory.zoomOut())
                    }) {
                        Text("-")
                    }
                }
            }
        }

        else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp)
            ) {

                // ===== BUTTONY =====
                item {
                    Button(
                        onClick = onAddPlace,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dodaj atrakcję")
                    }
                }

                item {
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Wstecz")
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (places.isEmpty()) {
                    item {
                        Text("Brak atrakcji tego dnia")
                    }
                } else {
                    items(places) { place ->

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(place.name, style = MaterialTheme.typography.titleMedium)
                            Text(place.time)

                            if (place.description.isNotBlank()) {
                                Text(place.description)
                            }

                            Text(place.category)
                        }

                        Divider()
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Dziennik podróży",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {Spacer(modifier = Modifier.height(16.dp))}

                item {
                    OutlinedTextField(
                        value = newNote,
                        onValueChange = { newNote = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Napisz notatkę...") }
                    )
                }

                item {
                    Button(
                        onClick = {
                            if (newNote.isNotBlank()) {
                                viewModel.addDiaryNote(
                                    travel.id,
                                    date,
                                    newNote
                                )
                                newNote = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dodaj notatkę")
                    }
                }


                items(notes) { note ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(note.text)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = java.text.SimpleDateFormat("HH:mm")
                                    .format(java.util.Date(note.createdAt)),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}