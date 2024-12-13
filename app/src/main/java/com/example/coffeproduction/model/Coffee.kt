package com.example.coffeproduction.model

data class Coffee(
    var id: Long = 0L,
    var name: String = "",
    var description: String = "",
    var aroma: Int = 0,
    var acidity: Int = 0,
    var bitterness: Int = 0,
    var flavor: Int = 0,
    var price: Double = 0.0,
    var image: String = ""
) {
    constructor() : this(0L, "", "", 0, 0, 0, 0, 0.0, "")
}
