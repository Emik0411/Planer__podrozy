// dane o jednym miejscy zwrócone przez API
package com.example.planerpodrozy.model

data class Properties(
    val name: String? = null,
    val formatted: String? = null,
    val address_line1: String? = null,
    val city: String? = null,
    val country: String? = null
)