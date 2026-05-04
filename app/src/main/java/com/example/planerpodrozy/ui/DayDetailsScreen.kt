package com.example.planerpodrozy.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.viewmodel.MainViewModel
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items

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

    Column {

        Button(
            onClick = onAddPlace,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("➕ Dodaj atrakcję")
        }

        // lista atrakcji
        LazyColumn {
            items(places) { place ->
                Text("${place.time} - ${place.name}")
            }
        }
    }
}

