// formularz dodawania/edytowania podróży
package com.example.planerpodrozy.ui
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*
import com.example.planerpodrozy.model.Feature
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.annotations.MarkerOptions

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTravelScreen(
    viewModel: MainViewModel,
    travelToEdit: Travel? = null,
    onSave: (String, String, String, String, String, Double?, Double?) -> Unit,
    onBack: () -> Unit
) {


    var name by remember { mutableStateOf(travelToEdit?.name ?: "") }
    var location by remember { mutableStateOf(travelToEdit?.location ?: "") }
    var description by remember { mutableStateOf(travelToEdit?.description ?: "") }
    var startDate by remember { mutableStateOf(travelToEdit?.startDate ?: "") }
    var endDate by remember { mutableStateOf(travelToEdit?.endDate ?: "") }

    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    val places by viewModel.places.collectAsState()

    var selectedLat by remember { mutableStateOf(travelToEdit?.lat ?: 0.0) }
    var selectedLon by remember { mutableStateOf(travelToEdit?.lon ?: 0.0) }

    var locationSelectedFromList by remember { mutableStateOf(false) }

    var step by remember { mutableStateOf(1) }
    var showMapPreview by remember { mutableStateOf(false) }

    var refineMode by remember { mutableStateOf(false) }
    var refineText by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            if (travelToEdit == null) "Dodaj podróż" else "Edytuj podróż",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(16.dp))


        if (step == 1) {

            Text("Wybierz lokalizację")
            Spacer(Modifier.height(12.dp))



            Box {

                OutlinedTextField(
                    value = location,
                    onValueChange = {
                        location = it
                        viewModel.searchPlaces(it)
                        expanded = true

                        locationSelectedFromList = false
                        selectedLat = 0.0
                        selectedLon = 0.0
                        showMapPreview = false
                    },
                    label = { Text("Lokalizacja") },
                    modifier = Modifier.fillMaxWidth()
                )

                DropdownMenu(
                    expanded = expanded && places.isNotEmpty(),
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    places.forEach { place ->
                        DropdownMenuItem(
                            text = { Text(place.properties.formatted ?: "") },
                            onClick = {
                                location = place.properties.formatted ?: ""

                                val coords = place.geometry?.coordinates
                                selectedLon = coords?.getOrNull(0) ?: 0.0
                                selectedLat = coords?.getOrNull(1) ?: 0.0

                                locationSelectedFromList = true
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { refineMode = !refineMode },
                        modifier = Modifier.fillMaxWidth()

            ) {
                Text(if (refineMode) "Niedoprecyzuj" else "Doprecyzuj")
            }

            if (refineMode) {

                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = refineText,
                    onValueChange = { refineText = it },
                    label = { Text("Doprecyzuj") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {

                    val base = location.trim()
                    val refine = refineText.trim()

                    val query = if (refineMode && refine.isNotBlank()) {
                        "$base, $refine"
                    } else {
                        base
                    }

                    if (query.isBlank()) return@Button

                    showMapPreview = false

                    viewModel.getLatLonFromAddress(query) { lat, lon ->
                        selectedLat = lat ?: 0.0
                        selectedLon = lon ?: 0.0
                        showMapPreview = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sprawdź na mapie")
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = {
                    if (location.isNotBlank()) {

                        if (!locationSelectedFromList) {
                            val query = if (refineMode && refineText.isNotBlank()) {
                                "$location, $refineText"
                            } else {
                                location
                            }

                            viewModel.getLatLonFromAddress(query) { lat, lon ->
                                selectedLat = lat ?: 0.0
                                selectedLon = lon ?: 0.0
                            }
                        }

                        step = 2
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dalej")
            }

            Spacer(Modifier.height(12.dp))

            Button(onClick ={ onBack()},
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Wstecz")
            }


            if (showMapPreview) {

                Spacer(Modifier.height(12.dp))



                Spacer(Modifier.height(12.dp))

                AndroidView(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    factory = { context ->

                        val mapView = MapView(context).apply {
                            onCreate(null)
                            onStart()
                        }

                        mapView.getMapAsync { map ->

                            map.setStyle(
                                "https://basemaps.cartocdn.com/gl/voyager-gl-style/style.json"
                            ) {

                                val point = LatLng(selectedLat, selectedLon)

                                map.cameraPosition =
                                    org.maplibre.android.camera.CameraPosition.Builder()
                                        .target(point)
                                        .zoom(10.0)
                                        .build()

                                map.clear()

                                map.addMarker(
                                    MarkerOptions()
                                        .position(point)
                                        .title(location)
                                )
                            }
                        }

                        mapView
                    },
                    update = {}
                )
            }
        }


        if (step == 2) {

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nazwa") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))


            Box {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data rozpoczęcia") },
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showStartPicker = true }
                )
            }

            Spacer(Modifier.height(12.dp))


            Box {
                OutlinedTextField(
                    value = endDate,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Data zakończenia") },
                    modifier = Modifier.fillMaxWidth()
                )

                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showEndPicker = true }
                )
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {

                    val start = runCatching { java.time.LocalDate.parse(startDate) }.getOrNull()
                    val end = runCatching { java.time.LocalDate.parse(endDate) }.getOrNull()

                    if (start != null && end != null && !start.isAfter(end)) {

                        val base = location.trim()
                        val refine = refineText.trim()

                        val finalQuery = if (refineMode && refine.isNotBlank()) {
                            "$base"
                        } else {
                            base
                        }

                        onSave(
                            name,
                            finalQuery,
                            description,
                            startDate,
                            endDate,
                            selectedLat,
                            selectedLon
                        )

                    } else {
                        error = "Złe daty"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Zapisz")
            }

            Button(
                onClick = { step = 1 },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wstecz")
            }
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }


    if (showStartPicker) {
        val state = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                Button(onClick = {
                    startDate = state.selectedDateMillis?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    } ?: ""
                    showStartPicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }

    if (showEndPicker) {
        val state = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                Button(onClick = {
                    endDate = state.selectedDateMillis?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it))
                    } ?: ""
                    showEndPicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = state)
        }
    }
}