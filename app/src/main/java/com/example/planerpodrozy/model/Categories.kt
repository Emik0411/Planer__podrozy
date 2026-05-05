package com.example.planerpodrozy.model


data class Category(
    val name: String,
    val subcategories: List<String>
)

val categories = listOf(
    Category(
        "Zwiedzanie",
        listOf("Muzea", "Zabytki", "Kościoły")
    ),
    Category(
        "Natura",
        listOf("Parki", "Góry", "Plaże")
    ),
    Category(
        "Jedzenie",
        listOf("Restauracje", "Kawiarnie", "Fast food")
    ),
    Category(
        "Rozrywka",
        listOf("Kina", "Kluby")
    )
)