package com.example.planerpodrozy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.room.Room
import com.example.planerpodrozy.data.AppDatabase
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.ui.AddPlaceScreen
import com.example.planerpodrozy.ui.AddTravelScreen
import com.example.planerpodrozy.ui.DayDetailsScreen
import com.example.planerpodrozy.ui.MainScreen
import com.example.planerpodrozy.ui.TravelDetailsScreen
import com.example.planerpodrozy.viewmodel.MainViewModel
import org.osmdroid.config.Configuration
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer
import com.example.planerpodrozy.data.DiaryNote



class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "travel_db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val viewModel = MainViewModel(
            db.travelDao(),
            db.placeDao(),
            db.diaryNoteDao()
        )


        Configuration.getInstance().load(
            applicationContext,
            getSharedPreferences("osmdroid", MODE_PRIVATE)
        )

        setContent {

            var showAddScreen by remember { mutableStateOf(false) }
            var travelToEdit by remember { mutableStateOf<Travel?>(null) }
            var selectedTravel by remember { mutableStateOf<Travel?>(null) }
            var selectedDay by remember { mutableStateOf<String?>(null) }
            var showAddPlaceScreen by remember { mutableStateOf(false) }

            when {
                showAddPlaceScreen && selectedTravel != null && selectedDay != null -> {
                    AddPlaceScreen(
                        travelId = selectedTravel!!.id,
                        date = selectedDay!!,
                        viewModel = viewModel,
                        onSave = { name, category, time, description, lat, lon ->
                            viewModel.addPlace(
                                travelId = selectedTravel!!.id,
                                date = selectedDay!!,
                                name = name,
                                category = category,
                                time = time,
                                description = description,
                                lat = lat,
                                lon = lon
                            )
                            showAddPlaceScreen = false
                        },
                        onBack = { showAddPlaceScreen = false
                            selectedDay = null // opcjonalnie
                        }
                    )
                }

                selectedDay != null && selectedTravel != null -> {
                    DayDetailsScreen(
                        travel = selectedTravel!!,
                        date = selectedDay!!,
                        viewModel = viewModel,
                        onBack = {
                            selectedDay = null
                        },
                        onAddPlace = {
                            showAddPlaceScreen = true
                        }
                    )
                }

                selectedTravel != null -> {
                    TravelDetailsScreen(
                        travel = selectedTravel!!,
                        viewModel = viewModel,
                        onBack = { selectedTravel = null },
                        onDayClick = { selectedDay = it }
                    )
                }


                showAddScreen -> {
                    AddTravelScreen(
                        viewModel = viewModel,
                        travelToEdit = travelToEdit,
                        onSave = { name, location, desc, start, end, lat, lon ->

                            if (travelToEdit == null) {
                                viewModel.addTravel(name, location, desc, start, end, lat, lon)
                            } else {
                                viewModel.updateTravel(
                                    travelToEdit!!.copy(
                                        name = name,
                                        location = location,
                                        description = desc,
                                        startDate = start,
                                        endDate = end,
                                        lat = lat,
                                        lon = lon
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
                }

                else -> {

                    MainScreen(
                        viewModel = viewModel,
                        onAddClick = { showAddScreen = true },
                        onEditClick = {
                            travelToEdit = it
                            showAddScreen = true
                        },
                        onTravelClick = {
                            selectedTravel = it
                        }
                    )
                }
            }
        }
    }
}