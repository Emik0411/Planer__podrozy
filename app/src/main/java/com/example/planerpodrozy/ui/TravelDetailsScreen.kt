package com.example.planerpodrozy.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.planerpodrozy.data.Travel
import com.example.planerpodrozy.viewmodel.MainViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TravelDetailsScreen(
    travel: Travel,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onDayClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {

        Text(travel.name, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        Text("Lokalizacja: ${travel.location}")
        Text("Opis: ${travel.description}")
        Text("${travel.startDate} - ${travel.endDate}")

        Spacer(Modifier.height(16.dp))

        Button(onClick = onBack) {
            Text("Wróć")
        }

        val days = generateDays(travel.startDate, travel.endDate)

        Spacer(Modifier.height(16.dp))

        Text("Dni podróży:")

        LazyColumn {
            itemsIndexed(days) { index, date ->
                DayItem(
                    dayNumber = index + 1,
                    date = date,
                    onClick = {
                        onDayClick(date)
                    }
                )
            }
        }

    }


}

@RequiresApi(Build.VERSION_CODES.O)
fun generateDays(start: String, end: String): List<String> {
    val formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val startDate = java.time.LocalDate.parse(start, formatter)
    val endDate = java.time.LocalDate.parse(end, formatter)

    val days = mutableListOf<String>()
    var current = startDate

    while (!current.isAfter(endDate)) {
        days.add(current.toString())
        current = current.plusDays(1)
    }

    return days
}

@Composable
fun DayItem(
    dayNumber: Int,
    date: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = "Dzień $dayNumber - $date",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium
        )
    }
}