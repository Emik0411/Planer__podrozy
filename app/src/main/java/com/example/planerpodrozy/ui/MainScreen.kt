package com.example.planerpodrozy.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onAddClick: () -> Unit,
    onEditClick: (Travel) -> Unit
){

    val travels = viewModel.travels.collectAsState().value

    Column {

        TopAppBar(
            title = { Text("Planer podróży") },
            actions = {
                IconButton(onClick = onAddClick) {
                    Text("+")
                }
            }
        )

        LazyColumn(
            modifier = Modifier.padding(8.dp)
        ) {
            items(travels) { travel ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(Modifier.padding(12.dp)) {

                        Text(travel.name, style = MaterialTheme.typography.titleLarge)

                        Spacer(Modifier.height(4.dp))

                        Text(travel.location)

                        Spacer(Modifier.height(4.dp))

                        Text("${travel.startDate} - ${travel.endDate}")

                        Spacer(Modifier.height(4.dp))

                        Text(travel.description)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            onEditClick(travel)
                        }) {
                            Text("Edytuj")
                        }

                        TextButton(onClick = {
                            viewModel.deleteTravel(travel)
                        }) {
                            Text("Usuń")
                        }
                    }
                }
            }
        }
    }
}