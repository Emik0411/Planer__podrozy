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

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTravelScreen(
    viewModel: MainViewModel,
    travelToEdit: Travel? = null,
    onSave: (String, String, String, String, String, Double?, Double?) -> Unit,
    onBack: () -> Unit
) {

    // dane formularza
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

        // pole z nazwą podróży
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nazwa") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(Modifier.height(16.dp))

        // pole z lokalizacją
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
                        text = { Text(place.properties.formatted.toString()) },
                        onClick = {
                            location = place.properties.formatted.toString()

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

        Spacer(Modifier.height(16.dp))


        // pole na opis
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Opis") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        // pole z datą rozpoczęcia
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

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
                    // po kliknięciu pokazuje się kalendarz
                    .clickable { showStartPicker = true }
            )
        }

        Spacer(Modifier.height(8.dp))

        // pole z datą zakończenia
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {

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


        Spacer(Modifier.height(16.dp))

        // przycisk zapisz
        Button(
            onClick = {
                val start = runCatching { java.time.LocalDate.parse(startDate) }.getOrNull()
                val end = runCatching { java.time.LocalDate.parse(endDate) }.getOrNull()

                if (start != null && end != null && !start.isAfter(end)) {

                    if (locationSelectedFromList) {

                        if (selectedLat == 0.0 && selectedLon == 0.0) {
                            error = "Nie udało się pobrać współrzędnych"
                            return@Button
                        }

                        onSave(
                            name,
                            location,
                            description,
                            startDate,
                            endDate,
                            selectedLat,
                            selectedLon
                        )
                    }

                    else {
                        viewModel.getLatLonFromAddress(location) { lat, lon ->



                            onSave(
                                name,
                                location,
                                description,
                                startDate,
                                endDate,
                                lat,
                                lon
                            )
                        }
                    }

                } else {
                    error = "Złe daty"
                }
            }
        ) {
            Text("Zapisz")
        }

        TextButton(onClick = onBack) {
            Text("Wróć")
        }

        if (error.isNotEmpty()) {
            Text(error, color = MaterialTheme.colorScheme.error)
        }
    }

    // kalendarz (rozpoczęcie)
    if (showStartPicker) {
        val state = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showStartPicker = false },
            confirmButton = {
                Button(onClick = {
                    // zamiana na tekst
                    startDate = state.selectedDateMillis?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(it))
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

    // kalendarz (zakończenie)
    if (showEndPicker) {
        val state = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showEndPicker = false },
            confirmButton = {
                Button(onClick = {
                    endDate = state.selectedDateMillis?.let {
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(it))
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