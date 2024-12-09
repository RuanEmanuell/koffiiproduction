package com.example.coffeproduction.model

data class Coffe(
    val id: Number,
    val name: String,
    val description: String,
    val aroma: Int,
    val acidity: Int,
    val bitterness: Int,
    val flavor: Int,
    val price: Double,
    val image: String
)
