package com.example.planerpodrozy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.room.Room
import com.example.planerpodrozy.data.AppDatabase
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.ui.AddTravelScreen
import com.example.planerpodrozy.ui.MainScreen
import com.example.planerpodrozy.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // tworzenie bazy danych
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "travel_db"
        ).build()

        val viewModel = MainViewModel(db.travelDao())



        // pokazywanie ekranu
        setContent {
            var showAddScreen by remember { mutableStateOf(false) }
            var travelToEdit by remember { mutableStateOf<Travel?>(null) }

            if (showAddScreen) {
                AddTravelScreen(
                    viewModel = viewModel,
                    travelToEdit = travelToEdit,
                    onSave = { name, location, desc, start, end ->

                        if (travelToEdit == null) {
                            viewModel.addTravel(name, location, desc, start, end)
                        } else {
                            viewModel.updateTravel(
                                travelToEdit!!.copy(
                                    name = name,
                                    location = location,
                                    description = desc,
                                    startDate = start,
                                    endDate = end
                                )
                            )
                        }

                        showAddScreen = false
                        travelToEdit = null
                    },
                    onBack = {
                        showAddScreen = false
                        travelToEdit = null
                    }
                )
            } else {
                MainScreen(
                    viewModel = viewModel,
                    onAddClick = { showAddScreen = true },
                    onEditClick = {
                        travelToEdit = it
                        showAddScreen = true
                    }
                )
            }
        }
    }
}