package com.example.planerpodrozy.ui

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.data.Place
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import com.example.planerpodrozy.viewmodel.MainViewModel
import androidx.compose.material3.*
import androidx.compose.runtime.*


@Composable
fun AddPlaceScreen(
    travelId: Int,
    date: String,
    viewModel: MainViewModel,
    onSave: (String, String, String) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Zwiedzanie") }
    var time by remember { mutableStateOf("12:00") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Dodaj atrakcję", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nazwa miejsca") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CategoryDropdown(
            selected = category,
            onSelected = { category = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TimePickerField(
            time = time,
            onTimeSelected = { time = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onSave(name, category, time)
        }) {
            Text("Zapisz")
        }
    }
}

@Composable
fun CategoryDropdown(
    selected: String,
    onSelected: (String) -> Unit
) {
    val categories = listOf("Zwiedzanie", "Jedzenie", "Relaks", "Rozrywka")

    var expanded by remember { mutableStateOf(false) }

    Column {
        Text("Kategoria")

        Box {
            Text(
                text = selected,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = true }
                    .padding(12.dp)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach {
                    DropdownMenuItem(
                        text = { Text(it) },
                        onClick = {
                            onSelected(it)
                            expanded = false
                        }
                    )
                }
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