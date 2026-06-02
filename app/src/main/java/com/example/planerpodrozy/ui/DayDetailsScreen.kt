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
import com.example.planerpodrozy.data.Place

import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.Marker
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

    var showMap by remember { mutableStateOf(false) } // domyślnie LISTA

    var mapRef by remember { mutableStateOf<org.maplibre.android.maps.MapLibreMap?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { showMap = false }
            ) {
                Text("Lista")
            }

            Button(
                onClick = { showMap = true }
            ) {
                Text("Mapa")
            }
        }


        if (showMap) {

            Box {

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


        if (!showMap) {

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAddPlace,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Dodaj atrakcję")
            }

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
                                text = place.time,
                                style = MaterialTheme.typography.bodyMedium
                            )

                            if (place.description.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(place.description)
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
        }
    }
}