package com.example.planerpodrozy.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.model.Category
import com.example.planerpodrozy.model.Feature
import com.example.planerpodrozy.model.categories
import com.example.planerpodrozy.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaceScreen(
    travelId: Int,
    date: String,
    viewModel: MainViewModel,
    onSave: (String, String, String, Double, Double) -> Unit,
    onBack: () -> Unit
) {

    val travel by viewModel.getTravelById(travelId).collectAsState(initial = null)


    // w którym miejscu jesteśmy
    var step by remember { mutableStateOf(1) }

    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var selectedSubcategory by remember { mutableStateOf<String?>(null) }

    var selectedFeature by remember { mutableStateOf<Feature?>(null) }

    var suggestions by remember { mutableStateOf(listOf<Feature>()) }

    var name by remember { mutableStateOf("") }
    var nameQuery by remember { mutableStateOf("") }

    var manualMode by remember { mutableStateOf(false) }

    var time by remember { mutableStateOf("12:00") }


    Column(modifier = Modifier.padding(16.dp)) {

        Text("Dodaj atrakcję", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))


        if (step == 1) {

            Text("Wybierz kategorię")

            Spacer(modifier = Modifier.height(8.dp))

            categories.forEach { cat ->
                Text(
                    text = cat.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedCategory = cat
                            step = 2
                        }
                        .padding(12.dp)
                )
            }
        }


        if (step == 2) {

            Text("Wybierz podkategorię")
            Spacer(modifier = Modifier.height(8.dp))

            selectedCategory?.subcategories?.forEach { sub ->
                Text(
                    text = sub,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSubcategory = sub
                            step = 3
                        }
                        .padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { step = 1 }) {
                Text("Wstecz")
            }
        }


        if (step == 3) {

            LaunchedEffect(selectedSubcategory, travel?.id) {


                if (selectedSubcategory == null) return@LaunchedEffect

                viewModel.searchPlacesByCategory(
                    category = selectedSubcategory!!,
                    query = selectedSubcategory!!,
                    location = "circle:52.2297,21.0122,20000"
                ) { result ->
                    suggestions = result
                }
            }

            Text("Wybierz miejsce z listy")
            Spacer(modifier = Modifier.height(12.dp))

            if (suggestions.isNotEmpty()) {

                suggestions.forEach { item ->
                    Text(
                        text = item.properties.formatted
                            ?: item.properties.name
                            ?: "Brak nazwy",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedFeature = item

                                name = item.properties.formatted
                                    ?: item.properties.name
                                            ?: ""

                                nameQuery = name
                                step = 4
                            }
                            .padding(12.dp)
                    )
                }

            } else {
                Text("Brak propozycji dla tej lokalizacji")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Nie ma na liście? Dodaj własne miejsce",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        manualMode = true
                        selectedFeature = null
                        name = nameQuery
                        step = 4
                    }
                    .padding(12.dp),
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { step = 2 },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Wstecz")
            }
        }


        if (step == 4) {

            if (manualMode) {

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nazwa miejsca") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nameQuery,
                    onValueChange = { nameQuery = it },
                    label = { Text("Adres / lokalizacja") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Wybierz godzinę")
            Spacer(modifier = Modifier.height(8.dp))

            TimePickerField(
                time = time,
                onTimeSelected = { time = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onClick@{
                    val finalCategory =
                        selectedSubcategory
                            ?: selectedCategory?.name
                            ?: "Brak kategorii"

                    val coords = selectedFeature?.geometry?.coordinates

                    val lat = coords?.getOrNull(1)
                    val lon = coords?.getOrNull(0)

                    if (lat == null || lon == null) {
                        return@onClick
                    }
                    onSave(name, finalCategory, time, lat, lon)
                }
            ) {
                Text("Zapisz")
            }



            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = { step = 3
                manualMode = false}) {
                Text("Wstecz")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    time: String,
    onTimeSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    val state = rememberTimePickerState(
        initialHour = time.substringBefore(":").toIntOrNull() ?: 12,
        initialMinute = time.substringAfter(":").toIntOrNull() ?: 0,
        is24Hour = true
    )

    Column {

        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = time,
                onValueChange = {},
                readOnly = true,
                label = { Text("Godzina") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showDialog = true }
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton = {
                    Button(onClick = {
                        val selected =
                            "%02d:%02d".format(state.hour, state.minute)
                        onTimeSelected(selected)
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Anuluj")
                    }
                },
                text = {
                    TimePicker(state = state)
                }
            )
        }
    }
}